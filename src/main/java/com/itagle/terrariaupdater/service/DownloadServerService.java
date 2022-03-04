package com.itagle.terrariaupdater.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DownloadServerService {

    public void download(String url, String localFilename) {
        try {
            log.info("Downloading file in {}", localFilename);
            FileUtils.copyURLToFile(new URL(url), new File(localFilename));
        } catch (IOException e) {
            log.error("Unable to download server file", e);
        }
    }
}
