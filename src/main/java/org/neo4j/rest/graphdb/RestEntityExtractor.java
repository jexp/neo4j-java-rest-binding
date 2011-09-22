package org.neo4j.rest.graphdb;

import java.util.Map;


public class RestEntityExtractor implements RestResultConverter {
    private final RestAPI restApi;

    public RestEntityExtractor(RestAPI restApi) {
        this.restApi = restApi;
    }

    public Object convertFromRepresentation(RequestResult requestResult) {
        return convertFromRepresentation(requestResult.toMap());
    }

    public Object convertFromRepresentation(Object value) {
        if (value instanceof Map) {
            RestEntity restEntity = createRestEntity((Map) value);
            if (restEntity != null) return restEntity;
        }       
        return value;
    }

    RestEntity createRestEntity(Map data) {
        final String uri = (String) data.get("self");        
        if (uri == null || uri.isEmpty()) return null;
        if (uri.contains("/node/")) {            
            return new RestNode(data, restApi);
        }
        if (uri.contains("/relationship/")) {
            return new RestRelationship(data, restApi);
        }
        return null;
    }

    public boolean canHandle(Object value) {
        if (value instanceof Map) {
            final String uri = (String) ((Map)value).get("self");
            if (uri != null && (uri.contains("/node/") || uri.contains("/relationship/"))){
                return true;
            }
        }
        return false;
    }
}