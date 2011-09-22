package org.neo4j.rest.graphdb.index;

import com.sun.jersey.api.client.ClientResponse;
import org.neo4j.rest.graphdb.RequestResult;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author mh
 * @since 22.09.11
 */
public class RetrievedIndexInfo implements IndexInfo {
    private Map<String, ?> indexInfo;

    public RetrievedIndexInfo(RequestResult response) {
        if (response.statusIs(ClientResponse.Status.NO_CONTENT)) this.indexInfo = Collections.emptyMap();
        else this.indexInfo = (Map<String, ?>) response.toMap();
    }

    @Override
    public boolean checkConfig(String indexName, Map<String, String> config) {
        Map<String, String> existingConfig = (Map<String, String>) indexInfo.get(indexName);
        if (config == null) {
            return existingConfig != null;
        } else {
            if (existingConfig == null) {
                return false;
            } else {
                if (existingConfig.entrySet().containsAll(config.entrySet())) {
                    return true;
                } else {
                    throw new IllegalArgumentException("Index with the same name but different config exists!");
                }
            }
        }
    }

    @Override
    public String[] indexNames() {
        Set<String> keys = indexInfo.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    @Override
    public boolean exists(String indexName) {
        return indexInfo.containsKey(indexName);
    }

    @Override
    public Map<String, String> getConfig(String name) {
        return (Map<String, String>) indexInfo.get(name);
    }
}
