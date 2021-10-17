package com.mtn.madapi.payments.occ.occcharger.configuration;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration("occ-properties")
@Data
public class OccProperties {

    private List<OCCInstance> OCCInstances;

}
