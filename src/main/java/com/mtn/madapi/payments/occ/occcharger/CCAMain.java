package com.mtn.madapi.payments.occ.occcharger;

import com.mtn.madapi.payments.occ.occcharger.cca.CCAClient;
import lombok.extern.slf4j.Slf4j;
import org.jdiameter.api.*;
import org.jdiameter.server.impl.StackImpl;
import org.jdiameter.server.impl.helpers.XMLConfiguration;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Slf4j
public class CCAMain {
    //configuration files
    private static final String configFile = "client-jdiameter-config.xml";
    private static final String dictionaryFile = "dictionary.xml";
    //our destination
    private static final String serverHost = "127.0.0.1";
    private static final String serverPort = "3868";
    private static final long applicationID = 333333;
    private static ApplicationId authAppId = ApplicationId.createByAuthAppId(applicationID);
    private static AvpDictionary dictionary = AvpDictionary.INSTANCE;
    private Stack stack;
    private SessionFactory factory;

    private boolean finished = false;  //boolean telling if we finished our interaction

    private static CCAClient ccaClient = new CCAClient();

    {
        InputStream is = null;
        try {
            //Parse dictionary, it is used for user friendly info.
            is = new ClassPathResource(configFile).getInputStream();
            //log.info(IOUtils.toString(is));
            ccaClient.init(is, "clientID");
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CCAMain ccaMain = new CCAMain();
        //ccaMain.initStack();
        ccaMain.start();

        while (!ccaMain.finished()) {
            try {
                Thread.currentThread().sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void initStack() {
        log.info("Initializing Stack...");
        InputStream is = null;
        try {
            //Parse dictionary, it is used for user friendly info.
            dictionary.parseDictionary(this.getClass().getClassLoader().getResourceAsStream(dictionaryFile));
            log.info("AVP Dictionary successfully parsed.");

            this.stack = new StackImpl();
            //Parse stack configuration
            is = this.getClass().getClassLoader().getResourceAsStream(configFile);
            Configuration config = new XMLConfiguration(is);
            factory = stack.init(config);
            log.info("Stack Configuration successfully loaded.");
            //Print info about applicatio
            Set<ApplicationId> appIds = stack.getMetaData().getLocalPeer().getCommonApplications();

            log.info("Diameter Stack  :: Supporting " + appIds.size() + " applications.");
            for (org.jdiameter.api.ApplicationId x : appIds) {
                log.info("Diameter Stack  :: Common :: " + x);
            }
            is.close();
            //Register network req listener, even though we wont receive requests
            //this has to be done to inform stack that we support application
            Network network = stack.unwrap(Network.class);
            network.addNetworkReqListener(new NetworkReqListener() {

                @Override
                public Answer processRequest(Request request) {
                    //this wontbe called.
                    return null;
                }
            }, this.authAppId); //passing our example app id.

        } catch (Exception e) {
            e.printStackTrace();
            if (this.stack != null) {
                this.stack.destroy();
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            return;
        }

        MetaData metaData = stack.getMetaData();
        //ignore for now.
        if (metaData.getStackType() != StackType.TYPE_SERVER || metaData.getMinorVersion() <= 0) {
            stack.destroy();
            log.error("Incorrect driver");
            return;
        }

        try {
            log.info("Starting stack");
            stack.start();
            log.info("Stack is running.");

        } catch (Exception e) {
            e.printStackTrace();
            stack.destroy();
            return;
        }
        log.info("Stack initialization successfully completed.");

    }

    private void start() {
        try {
            //wait for connection to peer
            try {
                Thread.currentThread().sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //do send
            //this.session = this.factory.getNewSession("BadCustomSessionId;YesWeCanPassId;" + System.currentTimeMillis());
            ccaClient.sendEvent();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private boolean finished() {
        return this.finished;
    }

}
