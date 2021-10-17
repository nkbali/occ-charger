package com.mtn.madapi.payments.occ.occcharger.cca;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;

public class CCAClient extends AbstractClient {

    protected boolean sentINITIAL;
    protected boolean sentINTERIM;
    protected boolean sentTERMINATE;
    protected boolean sentEVENT;
    protected boolean receiveINITIAL;
    protected boolean receiveINTERIM;
    protected boolean receiveTERMINATE;
    protected boolean receiveEVENT;

    /**
     *
     */
    public CCAClient() {
        // TODO Auto-generated constructor stub
    }

    public void sendInitial() throws Exception {
        JCreditControlRequest initialRequest = super.createCCR(CC_REQUEST_TYPE_INITIAL, this.ccRequestNumber, super.clientCCASession);
        this.ccRequestNumber++;
        super.clientCCASession.sendCreditControlRequest(initialRequest);
        Utils.printMessage(log, super.stack.getDictionary(), initialRequest.getMessage(), true);
        this.sentINITIAL = true;
    }

    public void sendInterim() throws Exception {
        if (!receiveINITIAL) {
            throw new Exception();
        }
        JCreditControlRequest interimRequest = super.createCCR(CC_REQUEST_TYPE_INTERIM, this.ccRequestNumber, super.clientCCASession);
        this.ccRequestNumber++;
        super.clientCCASession.sendCreditControlRequest(interimRequest);
        Utils.printMessage(log, super.stack.getDictionary(), interimRequest.getMessage(), true);
        this.sentINTERIM = true;
    }

    public void sendTermination() throws Exception {
        if (!receiveINTERIM) {
            throw new Exception();
        }
        JCreditControlRequest terminateRequest = super.createCCR(CC_REQUEST_TYPE_TERMINATE, this.ccRequestNumber, super.clientCCASession);
        this.ccRequestNumber++;
        super.clientCCASession.sendCreditControlRequest(terminateRequest);
        Utils.printMessage(log, super.stack.getDictionary(), terminateRequest.getMessage(), true);
        this.sentTERMINATE = true;
    }

    public void sendEvent() throws Exception {
        JCreditControlRequest eventRequest = super.createCCR(CC_REQUEST_TYPE_EVENT, this.ccRequestNumber, super.clientCCASession);
        this.ccRequestNumber++;
        super.clientCCASession.sendCreditControlRequest(eventRequest);
        Utils.printMessage(log, super.stack.getDictionary(), eventRequest.getMessage(), true);

        this.sentEVENT = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.api.cca.ClientCCASessionListener#doCreditControlAnswer( org.jdiameter.api.cca.ClientCCASession,
     * org.jdiameter.api.cca.events.JCreditControlRequest, org.jdiameter.api.cca.events.JCreditControlAnswer)
     */
    @Override
    public void doCreditControlAnswer(ClientCCASession session, JCreditControlRequest request, JCreditControlAnswer answer)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        try {
            Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), false);
            switch (answer.getRequestTypeAVPValue()) {
                case CC_REQUEST_TYPE_INITIAL:
                    if (receiveINITIAL) {
                        fail("Received INITIAL more than once!", null);
                    }
                    receiveINITIAL = true;

                    break;
                case CC_REQUEST_TYPE_INTERIM:
                    if (receiveINTERIM) {
                        fail("Received INTERIM more than once!", null);
                    }
                    receiveINTERIM = true;

                    break;
                case CC_REQUEST_TYPE_TERMINATE:
                    if (receiveTERMINATE) {
                        fail("Received TERMINATE more than once!", null);
                    }
                    receiveTERMINATE = true;

                    break;
                case CC_REQUEST_TYPE_EVENT:
                    if (receiveEVENT) {
                        fail("Received EVENT more than once!", null);
                    }
                    receiveEVENT = true;
                    break;

                default:

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.api.cca.ClientCCASessionListener#doReAuthRequest(org.jdiameter .api.cca.ClientCCASession,
     * org.jdiameter.api.auth.events.ReAuthRequest)
     */
    @Override
    public void doReAuthRequest(ClientCCASession session, ReAuthRequest request)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        fail("Received \"ReAuthRequest\" event, request[" + request + "], on session[" + session + "]", null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.api.cca.ClientCCASessionListener#doOtherEvent(org.jdiameter .api.app.AppSession,
     * org.jdiameter.api.app.AppRequestEvent, org.jdiameter.api.app.AppAnswerEvent)
     */
    @Override
    public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
            throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);

    }

    // ------------ getters for some vars;

    public boolean isSentINITIAL() {
        return sentINITIAL;
    }

    public boolean isSentEVENT() {
        return sentEVENT;
    }

    public boolean isReceiveEVENT() {
        return receiveEVENT;
    }

    public boolean isSentINTERIM() {
        return sentINTERIM;
    }

    public boolean isSentTERMINATE() {
        return sentTERMINATE;
    }

    public boolean isReceiveINITIAL() {
        return receiveINITIAL;
    }

    public boolean isReceiveINTERIM() {
        return receiveINTERIM;
    }

    public boolean isReceiveTERMINATE() {
        return receiveTERMINATE;
    }

    // ------------ getters for some vars;

    @Override
    protected int getChargingUnitsTime() {
        // TODO Auto-generated method stub
        return 10;
    }

    @Override
    protected String getServiceContextId() {
        // TODO Auto-generated method stub
        return "tralalalal ID";
    }

}
