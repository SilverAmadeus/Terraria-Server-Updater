package com.itagle.terrariaupdater.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CheckRemoteVersionService {

    public HttpStatus checkForRemoteVersion(String serverZipDownloadUri) {
    
        log.info("Requesting Server to URL -> {}", serverZipDownloadUri);
    
        HttpStatus status = WebClient.builder()
                .baseUrl(serverZipDownloadUri)
                .build()
                .get()
                .exchangeToMono(response -> Mono.just(response.statusCode()))
                .block();
                    
        log.info("Response -> {}", status);

        return status;
    }
}
