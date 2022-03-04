package com.itagle.terrariaupdater.runners;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import com.itagle.terrariaupdater.TerrariaConfigurationProperties;
import com.itagle.terrariaupdater.entity.Version;
import com.itagle.terrariaupdater.repository.VersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableConfigurationProperties(value = TerrariaConfigurationProperties.class)
public class ExecuteRunner implements ApplicationRunner {

    @Autowired
    private VersionRepository repository;

    @Autowired
    private TerrariaConfigurationProperties properties;

    @Override
    public void run(ApplicationArguments args) {
        // Check local version in Database
        log.info("Configured path {}", properties.getInstallationPath());
        Version localVersion = repository.findTopByOrderByVersionDesc();
        // If local version in database is empty then we use the passed version and save it
        if (localVersion == null) {
            log.info("Could not find entry in Database, using passed version");
            localVersion = new Version(args.getOptionValues("version").get(0), Timestamp.valueOf(LocalDateTime.now()));
            log.info("Saving version -> {}", localVersion);
            repository.save(localVersion);
        } else {
            log.info("Found version {} in Database -> {} - Using this as reference",
                    localVersion.getVersion(), localVersion);
        }
    }
}
