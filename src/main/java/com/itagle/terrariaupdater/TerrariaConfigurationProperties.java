package com.itagle.terrariaupdater;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "terraria")
@Data
public class TerrariaConfigurationProperties {
    
    private String installationPath;
    private String serverPropertiesPath;
    private String serverPropertiesFile;
}
