package com.mtn.madapi.payments.occ.occcharger.cca;

import org.apache.log4j.Logger;
import org.jdiameter.api.*;
import org.jdiameter.api.validation.ValidatorLevel;
import org.jdiameter.client.impl.StackImpl;
import org.jdiameter.client.impl.helpers.XMLConfiguration;
import org.jdiameter.common.impl.validation.DictionaryImpl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;

public class StackCreator extends StackImpl {

    private static Logger logger = Logger.getLogger(StackCreator.class);

    public StackCreator() {
        super();
    }

    public StackCreator(InputStream streamConfig, NetworkReqListener networkReqListener, EventListener<Request, Answer> eventListener, String dooer,
                        Boolean isServer, ApplicationId... appIds) throws Exception {
        init(isServer ? new XMLConfiguration(streamConfig) : new org.jdiameter.client.impl.helpers.XMLConfiguration(streamConfig), networkReqListener,
                eventListener, dooer, isServer, appIds);
    }

    public StackCreator(String stringConfig, NetworkReqListener networkReqListener, EventListener<Request, Answer> eventListener, String dooer, Boolean isServer,
                        ApplicationId... appIds) throws Exception {
        init(isServer ? new XMLConfiguration(new ByteArrayInputStream(stringConfig.getBytes())) : new org.jdiameter.client.impl.helpers.XMLConfiguration(
                new ByteArrayInputStream(stringConfig.getBytes())), networkReqListener, eventListener, dooer, isServer, appIds);
    }

    public void init(String stringConfig, NetworkReqListener networkReqListener, EventListener<Request, Answer> eventListener, String dooer, Boolean isServer,
                     ApplicationId... appIds) throws Exception {
        this.init(isServer ? new XMLConfiguration(new ByteArrayInputStream(stringConfig.getBytes())) :
                new org.jdiameter.client.impl.helpers.XMLConfiguration(new ByteArrayInputStream(
                        stringConfig.getBytes())), networkReqListener, eventListener, dooer, isServer, appIds);
    }

    public void init(InputStream streamConfig, NetworkReqListener networkReqListener, EventListener<Request, Answer> eventListener, String dooer,
                     Boolean isServer, ApplicationId... appIds) throws Exception {
        this.init(isServer ? new XMLConfiguration(streamConfig) :
                new org.jdiameter.client.impl.helpers.XMLConfiguration(streamConfig), networkReqListener, eventListener, dooer, isServer, appIds);
    }

    public void init(Configuration config, NetworkReqListener networkReqListener, EventListener<Request, Answer> eventListener, String identifier,
                     Boolean isServer, ApplicationId... appIds) throws Exception {
        // local one
        try {
            this.init(config);

            // Let it stabilize...
            Thread.sleep(500);

            // Let's do it right and enable all validation levels!
            DictionaryImpl.INSTANCE.setEnabled(true);
            DictionaryImpl.INSTANCE.setReceiveLevel(ValidatorLevel.ALL);
            DictionaryImpl.INSTANCE.setSendLevel(ValidatorLevel.ALL);

            Network network = unwrap(Network.class);

            if (appIds != null) {

                for (ApplicationId appId : appIds) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Diameter " + identifier + " :: Adding Listener for [" + appId + "].");
                    }
                    network.addNetworkReqListener(networkReqListener, appId);
                }

                if (logger.isInfoEnabled()) {
                    logger.info("Diameter " + identifier + " :: Supporting " + appIds.length + " applications.");
                }
            }
            else {
                Set<ApplicationId> stackAppIds = getMetaData().getLocalPeer().getCommonApplications();

                for (ApplicationId appId : stackAppIds) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Diameter " + identifier + " :: Adding Listener for [" + appId + "].");
                    }
                    network.addNetworkReqListener(networkReqListener, appId);
                }

                if (logger.isInfoEnabled()) {
                    logger.info("Diameter " + identifier + " :: Supporting " + stackAppIds.size() + " applications.");
                }
            }
        }
        catch (Exception e) {
            logger.error("Failure creating stack '" + identifier + "'", e);
        }

    }

}
