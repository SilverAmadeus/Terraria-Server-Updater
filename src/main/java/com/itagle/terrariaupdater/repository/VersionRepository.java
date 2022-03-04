package com.itagle.terrariaupdater.repository;

import com.itagle.terrariaupdater.entity.Version;
import org.springframework.data.repository.CrudRepository;

public interface VersionRepository extends CrudRepository<Version, String> {
    
    Version findByVersion(String version);
    
    Version findTopByOrderByVersionDesc();
}
