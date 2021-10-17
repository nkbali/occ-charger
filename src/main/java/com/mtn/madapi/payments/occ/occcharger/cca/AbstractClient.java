package com.mtn.madapi.payments.occ.occcharger.cca;

import org.jdiameter.api.*;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ClientCCASessionListener;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.common.api.app.cca.ClientCCASessionState;
import org.jdiameter.common.api.app.cca.IClientCCASessionContext;
import org.jdiameter.common.impl.app.cca.CCASessionFactoryImpl;
import org.jdiameter.common.impl.app.cca.JCreditControlRequestImpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class AbstractClient extends TBase implements ClientCCASessionListener, IClientCCASessionContext {

    // NOTE: implementing NetworkReqListener since its required for stack to
    // know we support it... ech.

    protected static final int CC_REQUEST_TYPE_INITIAL = 1;
    protected static final int CC_REQUEST_TYPE_INTERIM = 2;
    protected static final int CC_REQUEST_TYPE_TERMINATE = 3;
    protected static final int CC_REQUEST_TYPE_EVENT = 4;

    protected ClientCCASession clientCCASession;
    protected int ccRequestNumber = 0;
    protected List<StateChange<ClientCCASessionState>> stateChanges = new ArrayList<StateChange<ClientCCASessionState>>(); // state changes

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

    // ----------- delegate methods so

    public void start() throws IllegalDiameterStateException, InternalException {
        stack.start();
    }

    public void start(Mode mode, long timeOut, TimeUnit timeUnit) throws IllegalDiameterStateException, InternalException {
        stack.start(mode, timeOut, timeUnit);
    }

    public void stop(long timeOut, TimeUnit timeUnit, int disconnectCause) throws IllegalDiameterStateException, InternalException {
        stack.stop(timeOut, timeUnit, disconnectCause);
    }

    public void stop(int disconnectCause) {
        stack.stop(disconnectCause);
    }

    // ----------- conf parts

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.cca.IClientCCASessionContext# getDefaultTxTimerValue()
     */
    @Override
    public long getDefaultTxTimerValue() {
        return 10;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.api.cca.ClientCCASessionListener#getDefaultDDFHValue()
     */
    @Override
    public int getDefaultDDFHValue() {
        // DDFH_CONTINUE: 1
        return 1;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.api.cca.ClientCCASessionListener#getDefaultCCFHValue()
     */
    @Override
    public int getDefaultCCFHValue() {
        // CCFH_CONTINUE: 1
        return 1;
    }

    // ----------- should not be called..

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

    // ------------ leave those

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.cca.IClientCCASessionContext#txTimerExpired (org.jdiameter.api.cca.ClientCCASession)
     */
    @Override
    public void txTimerExpired(ClientCCASession session) {
        // NOP
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.cca.IClientCCASessionContext# grantAccessOnDeliverFailure(org.jdiameter.api.cca.ClientCCASession,
     * org.jdiameter.api.Message)
     */
    @Override
    public void grantAccessOnDeliverFailure(ClientCCASession clientCCASessionImpl, Message request) {
        // NOP
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.cca.IClientCCASessionContext# denyAccessOnDeliverFailure(org.jdiameter.api.cca.ClientCCASession,
     * org.jdiameter.api.Message)
     */
    @Override
    public void denyAccessOnDeliverFailure(ClientCCASession clientCCASessionImpl, Message request) {
        // NOP
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.cca.IClientCCASessionContext# grantAccessOnTxExpire(org.jdiameter.api.cca.ClientCCASession)
     */
    @Override
    public void grantAccessOnTxExpire(ClientCCASession clientCCASessionImpl) {
        // NOP
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.cca.IClientCCASessionContext# denyAccessOnTxExpire(org.jdiameter.api.cca.ClientCCASession)
     */
    @Override
    public void denyAccessOnTxExpire(ClientCCASession clientCCASessionImpl) {
        // NOP
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.cca.IClientCCASessionContext# grantAccessOnFailureMessage(org.jdiameter.api.cca.ClientCCASession)
     */
    @Override
    public void grantAccessOnFailureMessage(ClientCCASession clientCCASessionImpl) {
        // NOP
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.cca.IClientCCASessionContext# denyAccessOnFailureMessage(org.jdiameter.api.cca.ClientCCASession)
     */
    @Override
    public void denyAccessOnFailureMessage(ClientCCASession clientCCASessionImpl) {
        // NOP
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jdiameter.common.api.app.cca.IClientCCASessionContext# indicateServiceError(org.jdiameter.api.cca.ClientCCASession)
     */
    @Override
    public void indicateServiceError(ClientCCASession clientCCASessionImpl) {
        // NOP
    }

    // ---------- some helper methods.
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

    public String getSessionId() {
        return this.clientCCASession.getSessionId();
    }

    public void fetchSession(String sessionId) throws InternalException {
        this.clientCCASession = stack.getSession(sessionId, ClientCCASession.class);
    }

    public ClientCCASession getSession() {
        return this.clientCCASession;
    }

    public List<StateChange<ClientCCASessionState>> getStateChanges() {
        return stateChanges;
    }

    protected abstract int getChargingUnitsTime();

    protected abstract String getServiceContextId();
}
