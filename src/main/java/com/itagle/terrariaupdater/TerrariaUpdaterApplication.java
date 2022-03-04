package com.itagle.terrariaupdater;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.itagle.terrariaupdater.entity.Version;
import com.itagle.terrariaupdater.repository.VersionRepository;
import com.itagle.terrariaupdater.service.CheckRemoteVersionService;
import com.itagle.terrariaupdater.service.DownloadServerService;
import com.itagle.terrariaupdater.service.SetupServerService;
import com.itagle.terrariaupdater.service.UncompressServerFileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class TerrariaUpdaterApplication {

	@Autowired
    private VersionRepository repository;

	@Autowired
	private CheckRemoteVersionService checkRemoteVersionService;

    @Autowired
    private DownloadServerService downloadServerService;

    @Autowired
    private UncompressServerFileService uncompressServerFileService;

    @Autowired
    private SetupServerService setupServerService;

	@Autowired
    private TerrariaConfigurationProperties properties;

	private static final String DEDICATED_SERVER_URI = "https://terraria.org/api/download/pc-dedicated-server/";

	@Scheduled(cron = "${terraria.cron.downloadAttempt}")
	public void scheduledServerUpdate() throws IOException, InterruptedException {

		log.info("Starting scheduled Server Update");
		Version localVersion = repository.findTopByOrderByVersionDesc();
		Version newVersion = calculateNewVersion(localVersion);
		String zipFilename = generateZipFilenameString(newVersion);
		String zipDownloadUri = generateDownloadUri(zipFilename);

		HttpStatus status = checkRemoteVersionService.checkForRemoteVersion(zipDownloadUri);

		if (!status.is2xxSuccessful()) {
			log.info("Could not find version newer than {}", localVersion.getVersion());
			log.info("Sticking with current version");
		} else {
			// Download Terraria ZIP file and install
			downloadServerService.download(zipDownloadUri, properties.getInstallationPath() + "\\" + zipFilename);
			uncompressServerFileService.unzip(properties.getInstallationPath() + "\\" + zipFilename, properties.getInstallationPath());
			// Close Current running server and verify
			setupServerService.stopServer(properties.getServerPropertiesPath());
			log.info("Waiting 30 seconds before uninstalling service");
			Thread.sleep(30000);
			// Uninstall current service
			setupServerService.uninstallServer(properties.getServerPropertiesPath());
			// Copy new file to the installation directory
			setupServerService.copyNewServerFile(properties.getInstallationPath() + "\\" + newVersion.getVersion() + "\\Windows", properties.getServerPropertiesPath());
			// Install
			setupServerService.installServer(properties.getServerPropertiesPath());
			// Start
			setupServerService.startServer(properties.getServerPropertiesPath());
			log.info("Terraria Server succesfully upgraded to version {}", newVersion.getVersion());
			repository.save(newVersion);
			log.info("Saved version: {} to Database", newVersion.getVersion());
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	public void updateOnStartup() throws IOException, InterruptedException {
		scheduledServerUpdate();
	}

	public static void main(String[] args) {
		SpringApplication.run(TerrariaUpdaterApplication.class, args);
	}

	private String generateDownloadUri(String zipFilename) {
		return UriComponentsBuilder.fromUriString(DEDICATED_SERVER_URI)
				.path(zipFilename)
				.build(false)
				.toUriString();
	}

	private String generateZipFilenameString(Version newVersion) {
        return String.format("terraria-server-%s.zip", newVersion.getVersion());
    }

	private Version calculateNewVersion(Version localVersion) {
		Integer newVersionNumber = Integer.parseInt(localVersion.getVersion()) + 1;
		return new Version(newVersionNumber.toString(), Timestamp.valueOf(LocalDateTime.now()));
	}

}