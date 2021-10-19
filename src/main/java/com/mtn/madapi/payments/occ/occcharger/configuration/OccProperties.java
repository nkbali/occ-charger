package com.mtn.madapi.payments.occ.occcharger.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@ConfigurationProperties(prefix="occ")
@EnableConfigurationProperties
@Configuration
@Data
public class OccProperties {

    private List<OccInstance> OccInstances;

}
