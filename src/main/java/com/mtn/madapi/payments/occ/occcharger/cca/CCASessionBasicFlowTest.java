package com.mtn.madapi.payments.occ.occcharger.cca;


import org.jdiameter.api.*;
import org.mobicents.protocols.api.Server;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CCASessionBasicFlowTest {

    private CCAClient clientNode;
    private URI clientConfigURI;

    public CCASessionBasicFlowTest(String clientConfigUrl) throws Exception {
        super();
        this.clientConfigURI = new URI(clientConfigUrl);
    }

    public void setUp() throws Exception {
        try {
            this.clientNode = new CCAClient();

            this.clientNode.init(new FileInputStream(new File(this.clientConfigURI)), "CLIENT");
            this.clientNode.start(Mode.ANY_PEER, 10, TimeUnit.SECONDS);
            Stack stack = this.clientNode.getStack();

            List<Peer> peers = stack.unwrap(PeerTable.class).getPeerTable();
            if (peers.size() == 1) {
                // ok
            }
            else if (peers.size() > 1) {
                // works better with replicated, since disconnected peers are also listed
                boolean foundConnected = false;
                for (Peer p : peers) {
                    if (p.getState(PeerState.class).equals(PeerState.OKAY)) {
                        if (foundConnected) {
                            throw new Exception("Wrong number of connected peers: " + peers);
                        }
                        foundConnected = true;
                    }
                }
            }
            else {
                throw new Exception("Wrong number of connected peers: " + peers);
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void tearDown() {
        if (this.clientNode != null) {
            try {
                this.clientNode.stop(DisconnectCause.REBOOTING);
            }
            catch (Exception e) {

            }
            this.clientNode = null;
        }
    }

    public void testBasicFlow() throws Exception {
        try {
            // pain of parameter tests :) ?
            clientNode.sendInitial();
            waitForMessage();




            clientNode.sendInterim();
            waitForMessage();


            clientNode.sendTermination();
            waitForMessage();

            clientNode.sendEvent();

        }
        catch (Exception e) {
            e.printStackTrace();

        }

        StringBuilder sb = new StringBuilder();
        if (!clientNode.isReceiveINITIAL()) {
            sb = new StringBuilder("Did not receive INITIAL! ");
            sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

        }
        if (!clientNode.isReceiveINTERIM()) {
            sb = new StringBuilder("Did not receive INTERIM! ");
            sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

        }
        if (!clientNode.isReceiveTERMINATE()) {
            sb = new StringBuilder("Did not receive TERMINATE! ");
            sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

        }
        if (!clientNode.isPassed()) {
            sb = new StringBuilder();
            sb.append("Client ER:\n").append(clientNode.createErrorReport(this.clientNode.getErrors()));

        }

        System.out.println(sb.toString());

    }
public static Collection<Object[]> data() {
        String client = "configurations/functional-cca/config-client.xml";

        String replicatedClient = "configurations/functional-cca/replicated-config-client.xml";

        Class<CCASessionBasicFlowTest> t = CCASessionBasicFlowTest.class;
        client = t.getClassLoader().getResource(client).toString();
        replicatedClient = t.getClassLoader().getResource(replicatedClient).toString();

        return Arrays.asList(new Object[][] { { client }, { replicatedClient } });
    }

    private void waitForMessage() {
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
