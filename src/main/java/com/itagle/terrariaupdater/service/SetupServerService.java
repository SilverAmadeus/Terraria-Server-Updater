package com.itagle.terrariaupdater.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SetupServerService {
    //TODO: Use String Format
    public void stopServer(String serverPropertiesPath) {
        log.info("Stopping Terraria Server");
        runServerCommand(serverPropertiesPath + "\\" + "TerrariaService.exe stop", "stopped successfully");
    }

    public void startServer(String serverPropertiesPath) {
        log.info("Starting Terraria Server");
        runServerCommand(serverPropertiesPath + "\\" + "TerrariaService.exe start", "started successfully");
    }


    public void installServer(String serverProperties) {
        log.info("Installing Terraria Server");
        runServerCommand(serverProperties + "\\" + "TerrariaService.exe install", "installed successfully");
    }

    public void uninstallServer(String serverProperties) {
        log.info("Uninstalling Terraria Server");
        runServerCommand(serverProperties + "\\" + "TerrariaService.exe uninstall", "uninstalled successfully");
    }

    public void copyNewServerFile(String newServerVersionPath, String serverPropertiesPath) throws IOException {
        log.info("Copying new Terraria Server version into installation directory");
        File sourceFile = new File(newServerVersionPath + "\\" + "TerrariaServer.exe");
        File copiedFile = new File(serverPropertiesPath + "\\" + "TerrariaServer.exe");
        FileUtils.copyFile(sourceFile, copiedFile);
    }

    public void runServerCommand(String serverCommand, String expectedResult) {
        log.info("Executing {}", serverCommand);
        List<String> killOutput = runCmdCommand(serverCommand);
        if (!killOutput.stream().anyMatch(l -> l.contains(expectedResult))) {
            log.error("{}", killOutput);
            throw new IllegalStateException(String.format("Did not get expected result from command: %s", serverCommand));
        }
        log.info("{}", killOutput);
    }

    private List<String> runCmdCommand(String command) {
      List<String> output = new ArrayList<String>();
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
              output.add(line);
            }
            process.destroy();
            return output;
        } catch (Exception e) {
            log.error("Could not execute command {}", command);
            throw new IllegalStateException(e);
        }
    }
}
