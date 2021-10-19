package com.mtn.madapi.payments.occ.occcharger.cca;

import org.jdiameter.api.*;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.common.impl.app.cca.CCASessionFactoryImpl;
import org.jdiameter.common.impl.app.cca.JCreditControlRequestImpl;

import java.io.InputStream;
import java.util.UUID;

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

    @Override
    protected JCreditControlRequest createCCR(int ccRequestType, int requestNumber, ClientCCASession ccaSession) throws Exception {
        // Create Credit-Control-Request
        JCreditControlRequest ccr =
                new JCreditControlRequestImpl(ccaSession.getSessions().get(0).createRequest(JCreditControlRequest.code, getApplicationId(), getServerRealmName()));

        // AVPs present by default: Origin-Host, Origin-Realm, Session-Id,
        // Vendor-Specific-Application-Id, Destination-Realm
        AvpSet ccrAvps = ccr.getMessage().getAvps();

        // Add remaining AVPs ... from RFC 4006:
        // <CCR> ::= < Diameter Header: 272, REQ, PXY >
        // < Session-Id >
        // ccrAvps.addAvp(Avp.SESSION_ID, s.getSessionId());

        // { Origin-Host }
        ccrAvps.removeAvp(Avp.ORIGIN_HOST);
        ccrAvps.addAvp(Avp.ORIGIN_HOST, getClientURI(), true);

        // { Origin-Realm }
        // ccrAvps.addAvp(Avp.ORIGIN_REALM, realmName, true);

        // { Destination-Realm }
        // ccrAvps.addAvp(Avp.DESTINATION_REALM, realmName, true);

        // { Auth-Application-Id }
        // ccrAvps.addAvp(Avp.AUTH_APPLICATION_ID, 4);

        // { Service-Context-Id }
        // 8.42. Service-Context-Id AVP
        //
        // The Service-Context-Id AVP is of type UTF8String (AVP Code 461) and
        // contains a unique identifier of the Diameter credit-control service
        // specific document that applies to the request (as defined in section
        // 4.1.2). This is an identifier allocated by the service provider, by
        // the service element manufacturer, or by a standardization body, and
        // MUST uniquely identify a given Diameter credit-control service
        // specific document. The format of the Service-Context-Id is:
        //
        // "service-context" "@" "domain"
        //
        // service-context = Token
        //
        // The Token is an arbitrary string of characters and digits.
        //
        // 'domain' represents the entity that allocated the Service-Context-Id.
        // It can be ietf.org, 3gpp.org, etc., if the identifier is allocated by
        // a standardization body, or it can be the FQDN of the service provider
        // (e.g., provider.example.com) or of the vendor (e.g.,
        // vendor.example.com) if the identifier is allocated by a private
        // entity.
        //
        // This AVP SHOULD be placed as close to the Diameter header as
        // possible.
        //
        // Service-specific documents that are for private use only (i.e., to
        // one provider's own use, where no interoperability is deemed useful)
        // may define private identifiers without need of coordination.
        // However, when interoperability is wanted, coordination of the
        // identifiers via, for example, publication of an informational RFC is
        // RECOMMENDED in order to make Service-Context-Id globally available.
        String serviceContextId = getServiceContextId();
        if (serviceContextId == null) {
            serviceContextId = UUID.randomUUID().toString().replaceAll("-", "") + "@mss.mobicents.org";
        }
        ccrAvps.addAvp(Avp.SERVICE_CONTEXT_ID, serviceContextId, false);

        // { CC-Request-Type }
        // 8.3. CC-Request-Type AVP
        //
        // The CC-Request-Type AVP (AVP Code 416) is of type Enumerated and
        // contains the reason for sending the credit-control request message.
        // It MUST be present in all Credit-Control-Request messages. The
        // following values are defined for the CC-Request-Type AVP:
        //
        // INITIAL_REQUEST 1
        // An Initial request is used to initiate a credit-control session,
        // and contains credit control information that is relevant to the
        // initiation.
        //
        // UPDATE_REQUEST 2
        // An Update request contains credit-control information for an
        // existing credit-control session. Update credit-control requests
        // SHOULD be sent every time a credit-control re-authorization is
        // needed at the expiry of the allocated quota or validity time.
        // Further, additional service-specific events MAY trigger a
        // spontaneous Update request.
        //
        // TERMINATION_REQUEST 3
        // A Termination request is sent to terminate a credit-control
        // session and contains credit-control information relevant to the
        // existing session.
        //
        // EVENT_REQUEST 4
        // An Event request is used when there is no need to maintain any
        // credit-control session state in the credit-control server. This
        // request contains all information relevant to the service, and is
        // the only request of the service. The reason for the Event request
        // is further detailed in the Requested-Action AVP. The Requested-
        // Action AVP MUST be included in the Credit-Control-Request message
        // when CC-Request-Type is set to EVENT_REQUEST.
        ccrAvps.addAvp(Avp.CC_REQUEST_TYPE, ccRequestType);

        // { CC-Request-Number }
        // 8.2. CC-Request-Number AVP
        //
        // The CC-Request-Number AVP (AVP Code 415) is of type Unsigned32 and
        // identifies this request within one session. As Session-Id AVPs are
        // globally unique, the combination of Session-Id and CC-Request-Number
        // AVPs is also globally unique and can be used in matching credit-
        // control messages with confirmations. An easy way to produce unique
        // numbers is to set the value to 0 for a credit-control request of type
        // INITIAL_REQUEST and EVENT_REQUEST and to set the value to 1 for the
        // first UPDATE_REQUEST, to 2 for the second, and so on until the value
        // for TERMINATION_REQUEST is one more than for the last UPDATE_REQUEST.
        ccrAvps.addAvp(Avp.CC_REQUEST_NUMBER, requestNumber);

        // [ Destination-Host ]
        ccrAvps.removeAvp(Avp.DESTINATION_HOST);
        // ccrAvps.addAvp(Avp.DESTINATION_HOST, ccRequestType == 2 ?
        // serverURINode1 : serverURINode1, false);

        // [ User-Name ]
        // [ CC-Sub-Session-Id ]
        // [ Acct-Multi-Session-Id ]
        // [ Origin-State-Id ]
        // [ Event-Timestamp ]

        // *[ Subscription-Id ]
        // 8.46. Subscription-Id AVP
        //
        // The Subscription-Id AVP (AVP Code 443) is used to identify the end
        // user's subscription and is of type Grouped. The Subscription-Id AVP
        // includes a Subscription-Id-Data AVP that holds the identifier and a
        // Subscription-Id-Type AVP that defines the identifier type.
        //
        // It is defined as follows (per the grouped-avp-def of RFC 3588
        // [DIAMBASE]):
        //
        // Subscription-Id ::= < AVP Header: 443 >
        // { Subscription-Id-Type }
        // { Subscription-Id-Data }
        AvpSet subscriptionId = ccrAvps.addGroupedAvp(Avp.SUBSCRIPTION_ID);

        // 8.47. Subscription-Id-Type AVP
        //
        // The Subscription-Id-Type AVP (AVP Code 450) is of type Enumerated,
        // and it is used to determine which type of identifier is carried by
        // the Subscription-Id AVP.
        //
        // This specification defines the following subscription identifiers.
        // However, new Subscription-Id-Type values can be assigned by an IANA
        // designated expert, as defined in section 12. A server MUST implement
        // all the Subscription-Id-Types required to perform credit
        // authorization for the services it supports, including possible future
        // values. Unknown or unsupported Subscription-Id-Types MUST be treated
        // according to the 'M' flag rule, as defined in [DIAMBASE].
        //
        // END_USER_E164 0
        // The identifier is in international E.164 format (e.g., MSISDN),
        // according to the ITU-T E.164 numbering plan defined in [E164] and
        // [CE164].
        //
        // END_USER_IMSI 1
        // The identifier is in international IMSI format, according to the
        // ITU-T E.212 numbering plan as defined in [E212] and [CE212].
        //
        // END_USER_SIP_URI 2
        // The identifier is in the form of a SIP URI, as defined in [SIP].
        //
        // END_USER_NAI 3
        // The identifier is in the form of a Network Access Identifier, as
        // defined in [NAI].
        //
        // END_USER_PRIVATE 4
        // The Identifier is a credit-control server private identifier.
        subscriptionId.addAvp(Avp.SUBSCRIPTION_ID_TYPE, 2);

        // 8.48. Subscription-Id-Data AVP
        //
        // The Subscription-Id-Data AVP (AVP Code 444) is used to identify the
        // end user and is of type UTF8String. The Subscription-Id-Type AVP
        // defines which type of identifier is used.
        subscriptionId.addAvp(Avp.SUBSCRIPTION_ID_DATA, "sip:alexandre@mobicents.org", false);

        // [ Service-Identifier ]
        // [ Termination-Cause ]

        // [ Requested-Service-Unit ]
        // 8.18. Requested-Service-Unit AVP
        //
        // The Requested-Service-Unit AVP (AVP Code 437) is of type Grouped and
        // contains the amount of requested units specified by the Diameter
        // credit-control client. A server is not required to implement all the
        // unit types, and it must treat unknown or unsupported unit types as
        // invalid AVPs.
        //
        // The Requested-Service-Unit AVP is defined as follows (per the
        // grouped-avp-def of RFC 3588 [DIAMBASE]):
        //
        // Requested-Service-Unit ::= < AVP Header: 437 >
        // [ CC-Time ]
        // [ CC-Money ]
        // [ CC-Total-Octets ]
        // [ CC-Input-Octets ]
        // [ CC-Output-Octets ]
        // [ CC-Service-Specific-Units ]
        // *[ AVP ]
        AvpSet rsuAvp = ccrAvps.addGroupedAvp(Avp.REQUESTED_SERVICE_UNIT);

        // 8.21. CC-Time AVP
        //
        // The CC-Time AVP (AVP Code 420) is of type Unsigned32 and indicates
        // the length of the requested, granted, or used time in seconds.
        rsuAvp.addAvp(Avp.CC_TIME, getChargingUnitsTime());

        // [ Requested-Action ]
        // *[ Used-Service-Unit ]
        // 8.19. Used-Service-Unit AVP
        //
        // The Used-Service-Unit AVP is of type Grouped (AVP Code 446) and
        // contains the amount of used units measured from the point when the
        // service became active or, if interim interrogations are used during
        // the session, from the point when the previous measurement ended.
        //
        // The Used-Service-Unit AVP is defined as follows (per the grouped-
        // avp-def of RFC 3588 [DIAMBASE]):
        //
        // Used-Service-Unit ::= < AVP Header: 446 >
        // [ Tariff-Change-Usage ]
        // [ CC-Time ]
        // [ CC-Money ]
        // [ CC-Total-Octets ]
        // [ CC-Input-Octets ]
        // [ CC-Output-Octets ]
        // [ CC-Service-Specific-Units ]
        // *[ AVP ]

        // FIXME: alex :) ?
        // if(ccRequestNumber >= 1) {
        // AvpSet usedServiceUnit = ccrAvps.addGroupedAvp(Avp.USED_SERVICE_UNIT);
        // usedServiceUnit.addAvp(Avp.CC_TIME, this.partialCallDurationCounter);
        // System.out.println("USED SERVICE UNITS ==============================>"
        // + partialCallDurationCounter);
        // }
        // [ AoC-Request-Type ]
        // [ Multiple-Services-Indicator ]
        // *[ Multiple-Services-Credit-Control ]
        // *[ Service-Parameter-Info ]
        // [ CC-Correlation-Id ]
        // [ User-Equipment-Info ]
        // *[ Proxy-Info ]
        // *[ Route-Record ]
        // [ Service-Information ]
        // *[ AVP ]

        return ccr;
    }

    @Override
    public void init(InputStream configStream, String clientID) throws Exception {
        try {
            super.init(configStream, clientID, ApplicationId.createByAuthAppId(0, 4));
            CCASessionFactoryImpl creditControlSessionFactory = new CCASessionFactoryImpl(this.sessionFactory);
            sessionFactory.registerAppFacory(ClientCCASession.class, creditControlSessionFactory);

            creditControlSessionFactory.setStateListener(this);
            creditControlSessionFactory.setClientSessionListener(this);
            creditControlSessionFactory.setClientContextListener(this);
            this.clientCCASession = this.sessionFactory.getNewAppSession(this.sessionFactory.getSessionId("xxTESTxx"), getApplicationId(), ClientCCASession.class,
                    (Object) null);
        }
        finally {
            try {
                configStream.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
