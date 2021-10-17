package com.mtn.madapi.payments.occ.occcharger.cca;

import org.apache.log4j.Logger;
import org.jdiameter.api.*;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.client.api.ISessionFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class TBase implements EventListener<Request, Answer>, NetworkReqListener, StateChangeListener<AppSession> {

    protected final Logger log = Logger.getLogger(getClass());
    protected boolean passed = true;
    protected List<ErrorHolder> errors = new ArrayList<ErrorHolder>();

    // ------- those actually should come from conf... but..
    protected static final String clientHost = "127.0.0.1";
    protected static final String clientPort = "13868";
    protected static final String clientURI = "aaa://" + clientHost + ":" + clientPort;

    protected static final String serverHost = "127.0.0.1";
    protected static final String serverHost2 = "127.0.0.2";
    protected static final String serverPortNode1 = "4868";
    protected static final String serverPortNode2 = "4968";
    protected static final String serverURINode1 = "aaa://" + serverHost + ":" + serverPortNode1;
    protected static final String serverURINode2 = "aaa://" + serverHost2 + ":" + serverPortNode2;

    protected static final String serverRealm = "server.mobicents.org";
    protected static final String clientRealm = "client.mobicents.org";

    protected StackCreator stack;
    protected ISessionFactory sessionFactory;

    protected ApplicationId applicationId;

    public void init(InputStream configStream, String clientID, ApplicationId appId) throws Exception {
        this.applicationId = appId;
        stack = new StackCreator();
        stack.init(configStream, this, this, clientID, true, appId); // lets always pass
        this.sessionFactory = (ISessionFactory) this.stack.getSessionFactory();
    }

    protected void fail(String msg, Throwable e) {
        this.passed = false;
        ErrorHolder eh = new ErrorHolder(msg, e);
        this.errors.add(eh);
    }

    public boolean isPassed() {
        return passed;
    }

    public List<ErrorHolder> getErrors() {
        return errors;
    }

    public String createErrorReport(List<ErrorHolder> errors) {
        if (errors.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int index = 0; index < errors.size(); index++) {
                sb.append(errors.get(index));
                if (index + 1 < errors.size()) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
        else {
            return "";
        }
    }

    public ApplicationId getApplicationId() {
        return applicationId;
    }

    protected String getClientURI() {
        return clientURI;
    }

    protected String getServerRealmName() {
        return serverRealm;
    }

    protected String getClientRealmName() {
        return clientRealm;
    }

    public Stack getStack() {
        return this.stack;
    }

    /**
     * @return
     */
    protected String getServerURI() {
        return serverURINode1;
    }

    // --------- Default Implementation
    // --------- Depending on class it is overridden or by default makes test fail.
    @Override
    public void receivedSuccessMessage(Request request, Answer answer) {
        fail("Received \"SuccessMessage\" event, request[" + request + "], answer[" + answer + "]", null);
    }

    @Override
    public void timeoutExpired(Request request) {
        fail("Received \"Timoeout\" event, request[" + request + "]", null);
    }

    @Override
    public Answer processRequest(Request request) {
        fail("Received \"Request\" event, request[" + request + "]", null);
        return null;
    }

    // --- State Changes --------------------------------------------------------
    @Override
    public void stateChanged(Enum oldState, Enum newState) {
        // NOP
    }

    @Override
    public void stateChanged(AppSession source, Enum oldState, Enum newState) {
        // NOP
    }

}
