package com.mtn.madapi.payments.occ.occcharger.helpers;

import com.mtn.madapi.payments.occ.occcharger.cca.CCAClient;
import com.mtn.madapi.payments.occ.occcharger.client.manager.ClientManager;
import com.mtn.madapi.payments.occ.occcharger.configuration.OCCHost;
import com.mtn.madapi.payments.occ.occcharger.configuration.OCCInstance;
import com.mtn.madapi.payments.occ.occcharger.configuration.OccProperties;
import org.jdiameter.client.impl.helpers.XMLConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class PostInitialization {


    private final OccProperties occProperties;

    private final ClientManager clientManager;

    private final ResourceLoader resourceLoader;

    private final Resource resource;

    @Autowired
    public PostInitialization(OccProperties occProperties,
                              ClientManager clientManager,
                              ResourceLoader resourceLoader){
        this.occProperties = occProperties;
        this.clientManager = clientManager;
        this.resourceLoader = resourceLoader;
        this.resource = this.resourceLoader.getResource("client-config.xml");
    }

    @EventListener(ContextRefreshedEvent.class)
    public void createConnections(){
        occProperties.getOCCInstances().forEach(
                occInstance -> createCountryConnections(occInstance)
        );

    }

    private void createCountryConnections(OCCInstance occInstance){
        occInstance.getOccHosts().forEach(occHost -> clientManager.addConnection(occInstance.getCountryCode(), createConnection(occInstance, occHost)));
    }

    private CCAClient createConnection(OCCInstance occInstance, OCCHost occHost){
        try {
            CCAClient ccaClient = new CCAClient();
            InputStream inputStream = this.resource.getInputStream();
            XMLConfiguration xmlConfiguration = new XMLConfiguration(inputStream);
            //xmlConfiguration.
            //ccaClient.init(inputStream, "MADAPI");
            return ccaClient;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

}
