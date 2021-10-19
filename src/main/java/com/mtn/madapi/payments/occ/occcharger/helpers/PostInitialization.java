package com.mtn.madapi.payments.occ.occcharger.helpers;

import com.mtn.madapi.payments.occ.occcharger.cca.CCAClient;
import com.mtn.madapi.payments.occ.occcharger.cca.CCAXMLConfiguration;
import com.mtn.madapi.payments.occ.occcharger.client.manager.ClientManager;
import com.mtn.madapi.payments.occ.occcharger.configuration.OccHost;
import com.mtn.madapi.payments.occ.occcharger.configuration.OccInstance;
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

    private final XMLConfiguration xmlConfiguration;

    private final InputStream inputStream;

    @Autowired
    public PostInitialization(OccProperties occProperties,
                              ClientManager clientManager,
                              ResourceLoader resourceLoader) throws Exception {
        this.occProperties = occProperties;
        this.clientManager = clientManager;
        this.resourceLoader = resourceLoader;
        this.resource = this.resourceLoader.getResource("classpath:client-config.xml");
        this.inputStream = this.resource.getInputStream();
        this.xmlConfiguration = new XMLConfiguration(this.resource.getFile().getAbsolutePath(), null, null);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void createConnections(){

        if(occProperties.getOccInstances() != null)
            createConnection(occProperties.getOccInstances().get(0), occProperties.getOccInstances().get(0).getOccHosts().get(0));
//            occProperties.getOccInstances().forEach(
//                    occInstance -> createCountryConnections(occInstance)
//            );

    }

    private void createCountryConnections(OccInstance occInstance){
        occInstance.getOccHosts().forEach(occHost -> clientManager.addConnection(occInstance.getCountryCode(), createConnection(occInstance, occHost)));
    }

    private CCAClient createConnection(OccInstance occInstance, OccHost occHost){
        try {
            CCAClient ccaClient = new CCAClient();
//            
//            InputStream hostInputStream = new InputStream() {
//                @Override
//                public int read() throws IOException {
//                    return 0;
//                }
//            };
//            ccaClient.init(hostInputStream, "MADAPI");
            ccaClient.init(inputStream, "MADAPI");
            return ccaClient;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

}
