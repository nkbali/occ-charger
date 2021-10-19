package com.mtn.madapi.payments.occ.occcharger.configuration;

import lombok.Data;

import java.util.List;

@Data
public class OccInstance {
    private String countryCode;
    private List<OccHost> occHosts;
    private String port;
    private String originHost;
    private String originRealm;
    private String destinationRealm;
    private String productName;
    private String firmwareRevision;
    private String timezone;
    private String vendorId;
    private String serviceContextId;
    private String serviceIdentifierDebit;
    private String serviceIdentifierRefund;
    private String currencyCode;
    private String balanceFactor;
}
