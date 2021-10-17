package com.mtn.madapi.payments.occ.occcharger.client.manager;

import com.mtn.madapi.payments.occ.occcharger.cca.CCAClient;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Data
public class ClientManager {

    private Map<String, List<CCAClient>> connections = new ConcurrentHashMap<>();

    public void addConnection(String countryCode, CCAClient client){
        if(null == client)
            return;

        List<CCAClient> countryConnections;

        if(!connections.containsKey(countryCode)){
            countryConnections = new ArrayList<>();
        }
        else{
            countryConnections = connections.get(countryCode);
        }
        countryConnections.add(client);
        connections.put(countryCode, countryConnections);
    }

    public void removeConnection(String countryCode, CCAClient client){
        List<CCAClient> countryConnections;
        if(connections.containsKey(countryCode)){
            countryConnections = connections.get(countryCode);
            if(countryConnections.contains(client))
                countryConnections.remove(client);
            connections.put(countryCode, countryConnections);
        }
    }

    public void removeALLConnectionsForCountry(String countryCode, CCAClient client){
        List<CCAClient> countryConnections;
        if(connections.containsKey(countryCode)){
            connections.remove(countryCode);
        }
    }

    public CCAClient getConnectionForCountry(String countryCode){
        if(connections.containsKey(countryCode)){
            List<CCAClient> countryConnections = connections.get(countryCode);
            if(null == countryConnections || countryConnections.size() == 0)
                return null;
            int index = new Random().nextInt(countryConnections.size());
            return countryConnections.get(index);
        }

        return null;
    }

}
