package com.itagle.terrariaupdater.service;

import java.io.IOException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;

@Service
@Slf4j
public class UncompressServerFileService {
    
    public void unzip(String source, String destination) throws IOException {
        log.info("Unzipping file -> {}", source);
        try (ZipFile zipFile = new ZipFile(source)) {
            zipFile.extractAll(destination);
        } catch (IOException e) {
            log.error("Could not unzip file", e);
        }

    }
}
