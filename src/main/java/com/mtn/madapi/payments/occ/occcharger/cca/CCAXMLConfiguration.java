package com.mtn.madapi.payments.occ.occcharger.cca;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.impl.helpers.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class CCAXMLConfiguration extends EmptyConfiguration {
    public CCAXMLConfiguration(InputStream in) throws Exception {
        this(in, (Hashtable)null, (Hashtable)null, false);
    }

    public CCAXMLConfiguration(InputStream in, Hashtable<String, Object> attributes, Hashtable<String, Boolean> features) throws Exception {
        this(in, attributes, features, false);
    }

    public CCAXMLConfiguration(String filename) throws Exception {
        this(filename, (Hashtable)null, (Hashtable)null, false);
    }

    public CCAXMLConfiguration(String filename, Hashtable<String, Object> attributes, Hashtable<String, Boolean> features) throws Exception {
        this(filename, attributes, features, false);
    }

    protected CCAXMLConfiguration(Object in, Hashtable<String, Object> attributes, Hashtable<String, Boolean> features, boolean nop) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Iterator var6;
        String key;
        if (attributes != null) {
            var6 = attributes.keySet().iterator();

            while(var6.hasNext()) {
                key = (String)var6.next();
                factory.setAttribute(key, attributes.get(key));
            }
        }

        if (features != null) {
            var6 = features.keySet().iterator();

            while(var6.hasNext()) {
                key = (String)var6.next();
                factory.setFeature(key, (Boolean)features.get(key));
            }
        }

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document;
        if (in instanceof InputStream) {
            document = builder.parse((InputStream)in);
        } else {
            if (!(in instanceof String)) {
                throw new Exception("Unknown type of input data");
            }

            document = builder.parse(new File((String)in));
        }

        this.validate(document);
        this.processing(document);
    }

    protected void validate(Document document) throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Source schemaFile = new StreamSource(this.getClass().getClassLoader().getResourceAsStream("jdiameter-client.xsd"));
        Schema schema = factory.newSchema(schemaFile);
        Validator validator = schema.newValidator();
        validator.validate(new DOMSource(document));
    }

    protected void processing(Document document) {
        Element element = document.getDocumentElement();
        NodeList c = element.getChildNodes();

        for(int i = 0; i < c.getLength(); ++i) {
            String nodeName = c.item(i).getNodeName();
            if (nodeName.equals("LocalPeer")) {
                this.addLocalPeer(c.item(i));
            } else if (nodeName.equals("Parameters")) {
                this.addParameters(c.item(i));
            } else if (nodeName.equals("Network")) {
                this.addNetwork(c.item(i));
            } else if (nodeName.equals("Security")) {
                this.addSecurity(c.item(i));
            } else if (nodeName.equals("Extensions")) {
                this.addExtensions(c.item(i));
            }
        }

    }

    protected void addLocalPeer(Node node) {
        NodeList c = node.getChildNodes();
        if (node.getAttributes().getNamedItem("security_ref") != null) {
            this.add(Parameters.SecurityRef, node.getAttributes().getNamedItem("security_ref").getNodeValue());
        }

        for(int i = 0; i < c.getLength(); ++i) {
            String nodeName = c.item(i).getNodeName();
            if (nodeName.equals("URI")) {
                this.add(Parameters.OwnDiameterURI, this.getValue(c.item(i)));
            }

            this.addIPAddress(c.item(i));
            if (nodeName.equals("Realm")) {
                this.add(Parameters.OwnRealm, this.getValue(c.item(i)));
            }

            if (nodeName.equals("VendorID")) {
                this.add(Parameters.OwnVendorID, this.getLongValue(c.item(i)));
            }

            if (nodeName.equals("ProductName")) {
                this.add(Parameters.OwnProductName, this.getValue(c.item(i)));
            }

            if (nodeName.equals("FirmwareRevision")) {
                this.add(Parameters.OwnFirmwareRevision, this.getLongValue(c.item(i)));
            }

            if (nodeName.equals("Applications")) {
                this.addApplications(c.item(i));
            }
        }

    }

    protected void addIPAddress(Node node) {
        String nodeName = node.getNodeName();
        if (nodeName.equals("IPAddress")) {
            this.add(Parameters.OwnIPAddress, this.getValue(node));
        }

    }

    protected void addApplications(Node node) {
        NodeList c = node.getChildNodes();
        ArrayList<Configuration> items = new ArrayList();

        for(int i = 0; i < c.getLength(); ++i) {
            String nodeName = c.item(i).getNodeName();
            if (nodeName.equals("ApplicationID")) {
                items.add(this.addApplication(c.item(i)));
            }
        }

        this.add(Parameters.ApplicationId, (Configuration[])items.toArray(this.EMPTY_ARRAY));
    }

    protected Configuration addApplication(Node node) {
        NodeList c = node.getChildNodes();
        AppConfiguration e = getInstance();

        for(int i = 0; i < c.getLength(); ++i) {
            String nodeName = c.item(i).getNodeName();
            if (nodeName.equals("VendorId")) {
                e.add(Parameters.VendorId, this.getLongValue(c.item(i)));
            } else if (nodeName.equals("AuthApplId")) {
                e.add(Parameters.AuthApplId, this.getLongValue(c.item(i)));
            } else if (nodeName.equals("AcctApplId")) {
                e.add(Parameters.AcctApplId, this.getLongValue(c.item(i)));
            }
        }

        return e;
    }

    protected void addParameters(Node node) {
        NodeList c = node.getChildNodes();

        for(int i = 0; i < c.getLength(); ++i) {
            String nodeName = c.item(i).getNodeName();
            if (nodeName.equals("UseUriAsFqdn")) {
                this.add(Parameters.UseUriAsFqdn, Boolean.valueOf(this.getValue(c.item(i))));
            } else if (nodeName.equals("QueueSize")) {
                this.add(Parameters.QueueSize, this.getIntValue(c.item(i)));
            } else if (nodeName.equals("MessageTimeOut")) {
                this.add(Parameters.MessageTimeOut, this.getLongValue(c.item(i)));
            } else if (nodeName.equals("StopTimeOut")) {
                this.add(Parameters.StopTimeOut, this.getLongValue(c.item(i)));
            } else if (nodeName.equals("CeaTimeOut")) {
                this.add(Parameters.CeaTimeOut, this.getLongValue(c.item(i)));
            } else if (nodeName.equals("IacTimeOut")) {
                this.add(Parameters.IacTimeOut, this.getLongValue(c.item(i)));
            } else if (nodeName.equals("DwaTimeOut")) {
                this.add(Parameters.DwaTimeOut, this.getLongValue(c.item(i)));
            } else if (nodeName.equals("DpaTimeOut")) {
                this.add(Parameters.DpaTimeOut, this.getLongValue(c.item(i)));
            } else if (nodeName.equals("RecTimeOut")) {
                this.add(Parameters.RecTimeOut, this.getLongValue(c.item(i)));
            } else if (nodeName.equals("PeerFSMThreadCount")) {
                this.add(Parameters.PeerFSMThreadCount, this.getIntValue(c.item(i)));
            } else if (nodeName.equals("Statistics")) {
                this.addStatisticLogger(Parameters.Statistics, c.item(i));
            } else if (nodeName.equals("Concurrent")) {
                this.addConcurrent(Parameters.Concurrent, c.item(i));
            } else if (nodeName.equals("Dictionary")) {
                this.addDictionary(Parameters.Dictionary, c.item(i));
            } else {
                this.appendOtherParameter(c.item(i));
            }
        }

    }

    protected void addConcurrent(Parameters name, Node node) {
        NodeList c = node.getChildNodes();
        List<Configuration> items = new ArrayList();

        for(int i = 0; i < c.getLength(); ++i) {
            String nodeName = c.item(i).getNodeName();
            if (nodeName.equals("Entity")) {
                this.addConcurrentEntity(items, c.item(i));
            }
        }

        this.add(name, (Configuration[])items.toArray(new Configuration[items.size()]));
    }

    protected void addConcurrentEntity(List<Configuration> items, Node node) {
        AppConfiguration cfg = getInstance();
        String name = node.getAttributes().getNamedItem("name").getNodeValue();
        cfg.add(Parameters.ConcurrentEntityName, name);
        String size;
        if (node.getAttributes().getNamedItem("description") != null) {
            size = node.getAttributes().getNamedItem("description").getNodeValue();
            cfg.add(Parameters.ConcurrentEntityDescription, size);
        }

        if (node.getAttributes().getNamedItem("size") != null) {
            size = node.getAttributes().getNamedItem("size").getNodeValue();
            cfg.add(Parameters.ConcurrentEntityPoolSize, Integer.parseInt(size));
        }

        items.add(cfg);
    }

    protected void addStatisticLogger(Parameters name, Node node) {
        String pause = node.getAttributes().getNamedItem("pause").getNodeValue();
        String delay = node.getAttributes().getNamedItem("delay").getNodeValue();
        String enabled = node.getAttributes().getNamedItem("enabled").getNodeValue();
        String active_records;
        if (node.getAttributes().getNamedItem("active_records") != null) {
            active_records = node.getAttributes().getNamedItem("active_records").getNodeValue();
        } else {
            active_records = (String)Parameters.StatisticsActiveList.defValue();
        }

        this.add(name, getInstance().add(Parameters.StatisticsLoggerPause, Long.parseLong(pause)).add(Parameters.StatisticsLoggerDelay, Long.parseLong(delay)).add(Parameters.StatisticsEnabled, Boolean.parseBoolean(enabled)).add(Parameters.StatisticsActiveList, active_records));
    }

    protected void addDictionary(Parameters name, Node node) {
        AppConfiguration dicConfiguration = getInstance();
        Node param = node.getAttributes().getNamedItem("class");
        String receiveLevel;
        if (param != null) {
            receiveLevel = param.getNodeValue();
            dicConfiguration.add(Parameters.DictionaryClass, receiveLevel);
        }

        param = node.getAttributes().getNamedItem("enabled");
        if (param != null) {
            receiveLevel = param.getNodeValue();
            dicConfiguration.add(Parameters.DictionaryEnabled, Boolean.valueOf(receiveLevel));
        }

        param = node.getAttributes().getNamedItem("sendLevel");
        if (param != null) {
            receiveLevel = param.getNodeValue();
            dicConfiguration.add(Parameters.DictionarySendLevel, receiveLevel);
        }

        param = node.getAttributes().getNamedItem("receiveLevel");
        if (param != null) {
            receiveLevel = param.getNodeValue();
            dicConfiguration.add(Parameters.DictionaryReceiveLevel, receiveLevel);
        }

        this.add(name, dicConfiguration);
    }

    protected void appendOtherParameter(Node node) {
    }

    protected void addThreadPool(Node item) {
        AppConfiguration threadPoolConfiguration = EmptyConfiguration.getInstance();
        NamedNodeMap attributes = item.getAttributes();

        for(int index = 0; index < attributes.getLength(); ++index) {
            Node n = attributes.item(index);
            int v = Integer.parseInt(n.getNodeValue());
            if (n.getNodeName().equals("size")) {
                threadPoolConfiguration.add(Parameters.ThreadPoolSize, v);
            } else if (n.getNodeName().equals("priority")) {
                threadPoolConfiguration.add(Parameters.ThreadPoolPriority, v);
            }
        }

        if (!threadPoolConfiguration.isAttributeExist(Parameters.ThreadPoolSize.ordinal())) {
            threadPoolConfiguration.add(Parameters.ThreadPoolSize, Parameters.ThreadPoolSize.defValue());
        }

        if (!threadPoolConfiguration.isAttributeExist(Parameters.ThreadPoolPriority.ordinal())) {
            threadPoolConfiguration.add(Parameters.ThreadPoolPriority, Parameters.ThreadPoolPriority.defValue());
        }

        this.add(Parameters.ThreadPool, threadPoolConfiguration);
    }

    protected void addSecurity(Node node) {
        NodeList c = node.getChildNodes();
        List<Configuration> items = new ArrayList();

        for(int i = 0; i < c.getLength(); ++i) {
            String nodeName = c.item(i).getNodeName();
            if (nodeName.equals("SecurityData")) {
                items.add(this.addSecurityData(c.item(i)));
            }
        }

        this.add(Parameters.Security, (Configuration[])items.toArray(this.EMPTY_ARRAY));
    }

    protected Configuration addSecurityData(Node node) {
        AppConfiguration sd = getInstance().add(Parameters.SDName, node.getAttributes().getNamedItem("name").getNodeValue()).add(Parameters.SDProtocol, node.getAttributes().getNamedItem("protocol").getNodeValue()).add(Parameters.SDEnableSessionCreation, Boolean.valueOf(node.getAttributes().getNamedItem("enable_session_creation").getNodeValue())).add(Parameters.SDUseClientMode, Boolean.valueOf(node.getAttributes().getNamedItem("use_client_mode").getNodeValue()));
        NodeList c = node.getChildNodes();

        for(int i = 0; i < c.getLength(); ++i) {
            Node cnode = c.item(i);
            String nodeName = cnode.getNodeName();
            if (nodeName.equals("CipherSuites")) {
                sd.add(Parameters.CipherSuites, cnode.getTextContent().trim());
            }

            if (nodeName.equals("KeyData")) {
                sd.add(Parameters.KeyData, getInstance().add(Parameters.KDManager, cnode.getAttributes().getNamedItem("manager").getNodeValue()).add(Parameters.KDStore, cnode.getAttributes().getNamedItem("store").getNodeValue()).add(Parameters.KDFile, cnode.getAttributes().getNamedItem("file").getNodeValue()).add(Parameters.KDPwd, cnode.getAttributes().getNamedItem("pwd").getNodeValue()));
            }

            if (nodeName.equals("TrustData")) {
                sd.add(Parameters.TrustData, getInstance().add(Parameters.TDManager, cnode.getAttributes().getNamedItem("manager").getNodeValue()).add(Parameters.TDStore, cnode.getAttributes().getNamedItem("store").getNodeValue()).add(Parameters.TDFile, cnode.getAttributes().getNamedItem("file").getNodeValue()).add(Parameters.TDPwd, cnode.getAttributes().getNamedItem("pwd").getNodeValue()));
            }
        }

        return sd;
    }

    protected void addNetwork(Node node) {
        NodeList c = node.getChildNodes();

        for(int i = 0; i < c.getLength(); ++i) {
            String nodeName = c.item(i).getNodeName();
            if (nodeName.equals("Peers")) {
                this.addPeers(c.item(i));
            } else if (nodeName.equals("Realms")) {
                this.addRealms(c.item(i));
            }
        }

    }

    protected void addPeers(Node node) {
        NodeList c = node.getChildNodes();
        ArrayList<Configuration> items = new ArrayList();

        for(int i = 0; i < c.getLength(); ++i) {
            String nodeName = c.item(i).getNodeName();
            if (nodeName.equals("Peer")) {
                items.add(this.addPeer(c.item(i)));
            }
        }

        this.add(Parameters.PeerTable, (Configuration[])items.toArray(this.EMPTY_ARRAY));
    }

    protected void addRealms(Node node) {
        NodeList c = node.getChildNodes();
        ArrayList<Configuration> items = new ArrayList();

        for(int i = 0; i < c.getLength(); ++i) {
            String nodeName = c.item(i).getNodeName();
            if (nodeName.equals("Realm")) {
                items.add(this.addRealm(c.item(i)));
            }
        }

        this.add(Parameters.RealmTable, (Configuration[])items.toArray(this.EMPTY_ARRAY));
    }

    protected Configuration addPeer(Node node) {
        AppConfiguration peerConfig = getInstance().add(Parameters.PeerRating, new Integer(node.getAttributes().getNamedItem("rating").getNodeValue())).add(Parameters.PeerName, node.getAttributes().getNamedItem("name").getNodeValue());
        if (node.getAttributes().getNamedItem("ip") != null) {
            peerConfig.add(Parameters.PeerIp, node.getAttributes().getNamedItem("ip").getNodeValue());
        }

        if (node.getAttributes().getNamedItem("portRange") != null) {
            peerConfig.add(Parameters.PeerLocalPortRange, node.getAttributes().getNamedItem("portRange").getNodeValue());
        }

        if (node.getAttributes().getNamedItem("security_ref") != null) {
            peerConfig.add(Parameters.SecurityRef, node.getAttributes().getNamedItem("security_ref").getNodeValue());
        }

        return peerConfig;
    }

    protected Configuration addRealm(Node node) {
        AppConfiguration realmEntry = getInstance().add(Parameters.ApplicationId, new Configuration[]{this.addApplicationID(node.getChildNodes())}).add(org.jdiameter.server.impl.helpers.Parameters.RealmName, this.getAttrValue(node, "name")).add(org.jdiameter.server.impl.helpers.Parameters.RealmHosts, this.getAttrValue(node, "peers")).add(org.jdiameter.server.impl.helpers.Parameters.RealmLocalAction, this.getAttrValue(node, "local_action")).add(org.jdiameter.server.impl.helpers.Parameters.RealmEntryIsDynamic, Boolean.valueOf(this.getAttrValue(node, "dynamic"))).add(org.jdiameter.server.impl.helpers.Parameters.RealmEntryExpTime, Long.valueOf(this.getAttrValue(node, "exp_time")));
        NodeList childNodes = node.getChildNodes();

        for(int i = 0; i < childNodes.getLength(); ++i) {
            String nodeName = childNodes.item(i).getNodeName();
            if (nodeName.equals("Agent")) {
                realmEntry.add(Parameters.Agent, this.addAgent(childNodes.item(i)));
            }
        }

        return getInstance().add(Parameters.RealmEntry, realmEntry);
    }

    protected Configuration addAgent(Node node) {
        AppConfiguration agentConf = getInstance();
        NodeList agentChildren = node.getChildNodes();

        for(int index = 0; index < agentChildren.getLength(); ++index) {
            Node n = agentChildren.item(index);
            if (n.getNodeName().equals("Properties")) {
                agentConf.add(Parameters.Properties, (Configuration[])this.getProperties(n).toArray(this.EMPTY_ARRAY));
            }
        }

        return agentConf;
    }

    protected List<Configuration> getProperties(Node node) {
        List<Configuration> props = new ArrayList();
        NodeList propertiesChildren = node.getChildNodes();

        for(int index = 0; index < propertiesChildren.getLength(); ++index) {
            Node n = propertiesChildren.item(index);
            if (n.getNodeName().equals("Property")) {
                AppConfiguration property = getInstance();
                property.add(Parameters.PropertyName, n.getAttributes().getNamedItem(Parameters.PropertyName.name()).getNodeValue());
                property.add(Parameters.PropertyValue, n.getAttributes().getNamedItem(Parameters.PropertyValue.name()).getNodeValue());
                props.add(property);
            }
        }

        return props;
    }

    protected Configuration addApplicationID(NodeList node) {
        for(int i = 0; i < node.getLength(); ++i) {
            String nodeName = node.item(i).getNodeName();
            if (nodeName.equals("ApplicationID")) {
                return this.addApplicationID(node.item(i));
            }
        }

        return null;
    }

    protected Configuration addApplicationID(Node node) {
        NodeList c = node.getChildNodes();
        AppConfiguration e = getInstance();

        for(int i = 0; i < c.getLength(); ++i) {
            String nodeName = c.item(i).getNodeName();
            if (nodeName.equals("VendorId")) {
                e.add(Parameters.VendorId, this.getLongValue(c.item(i)));
            } else if (nodeName.equals("AuthApplId")) {
                e.add(Parameters.AuthApplId, this.getLongValue(c.item(i)));
            } else if (nodeName.equals("AcctApplId")) {
                e.add(Parameters.AcctApplId, this.getLongValue(c.item(i)));
            }
        }

        return e;
    }

    protected void addExtensions(Node node) {
        NodeList c = node.getChildNodes();

        for(int i = 0; i < c.getLength(); ++i) {
            String nodeName = c.item(i).getNodeName();
            if (nodeName.equals("MetaData")) {
                this.addInternalExtension(ExtensionPoint.InternalMetaData, this.getValue(c.item(i)));
            } else if (nodeName.equals("MessageParser")) {
                this.addInternalExtension(ExtensionPoint.InternalMessageParser, this.getValue(c.item(i)));
            } else if (nodeName.equals("ElementParser")) {
                this.addInternalExtension(ExtensionPoint.InternalElementParser, this.getValue(c.item(i)));
            } else if (nodeName.equals("RouterEngine")) {
                this.addInternalExtension(ExtensionPoint.InternalRouterEngine, this.getValue(c.item(i)));
            } else if (nodeName.equals("PeerController")) {
                this.addInternalExtension(ExtensionPoint.InternalPeerController, this.getValue(c.item(i)));
            } else if (nodeName.equals("RealmController")) {
                this.addInternalExtension(ExtensionPoint.InternalRealmController, this.getValue(c.item(i)));
            } else if (nodeName.equals("SessionFactory")) {
                this.addInternalExtension(ExtensionPoint.InternalSessionFactory, this.getValue(c.item(i)));
            } else if (nodeName.equals("TransportFactory")) {
                this.addInternalExtension(ExtensionPoint.InternalTransportFactory, this.getValue(c.item(i)));
            } else if (nodeName.equals("Connection")) {
                this.addInternalExtension(ExtensionPoint.InternalConnectionClass, this.getValue(c.item(i)));
            } else if (nodeName.equals("PeerFsmFactory")) {
                this.addInternalExtension(ExtensionPoint.InternalPeerFsmFactory, this.getValue(c.item(i)));
            } else if (nodeName.equals("StatisticFactory")) {
                this.addInternalExtension(ExtensionPoint.InternalStatisticFactory, this.getValue(c.item(i)));
            } else if (nodeName.equals("ConcurrentFactory")) {
                this.addInternalExtension(ExtensionPoint.InternalConcurrentFactory, this.getValue(c.item(i)));
            } else if (nodeName.equals("ConcurrentEntityFactory")) {
                this.addInternalExtension(ExtensionPoint.InternalConcurrentEntityFactory, this.getValue(c.item(i)));
            } else if (nodeName.equals("SessionDatasource")) {
                this.addInternalExtension(ExtensionPoint.InternalSessionDatasource, this.getValue(c.item(i)));
            } else if (nodeName.equals("TimerFacility")) {
                this.addInternalExtension(ExtensionPoint.InternalTimerFacility, this.getValue(c.item(i)));
            } else if (nodeName.equals("AgentRedirect")) {
                this.addInternalExtension(ExtensionPoint.InternalAgentRedirect, this.getValue(c.item(i)));
            } else if (nodeName.equals("AgentConfiguration")) {
                this.add(ExtensionPoint.InternalAgentConfiguration, this.getValue(c.item(i)));
            } else if (nodeName.equals("StatisticProcessor")) {
                this.addInternalExtension(ExtensionPoint.InternalStatisticProcessor, this.getValue(c.item(i)));
            } else {
                this.appendOtherExtension(c.item(i));
            }
        }

    }

    protected void addInternalExtension(Ordinal ep, String value) {
        Configuration[] extensionConfs = this.getChildren(Parameters.Extensions.ordinal());
        AppConfiguration internalExtensions = (AppConfiguration)extensionConfs[ExtensionPoint.Internal.id()];
        internalExtensions.add(ep, value);
    }

    private void appendOtherExtension(Node item) {
    }

    protected Long getLongValue(Node node) {
        return new Long(this.getValue(node));
    }

    protected Integer getIntValue(Node node) {
        return new Integer(this.getValue(node));
    }

    protected String getValue(Node node) {
        return node.getAttributes().getNamedItem("value").getNodeValue();
    }

    protected String getAttrValue(Node node, String name) {
        return node.getAttributes().getNamedItem(name).getNodeValue();
    }
}
