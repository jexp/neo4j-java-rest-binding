package org.neo4j.rest.graphdb;

import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.rest.graphdb.index.SimpleIndexHits;

import java.util.Collection;

/**
 * @author mh
 * @since 22.09.11
 */
public class RestIndexHitsConverter<S extends PropertyContainer> implements RestResultConverter {
    private final RestAPI restAPI;
    private final Class<S> entityType;

    public RestIndexHitsConverter(RestAPI restAPI,Class<S> entityType) {
        this.restAPI = restAPI;
        this.entityType = entityType;
    }

    public IndexHits<S> convertFromRepresentation(RequestResult response) {
        Collection hits = (Collection) response.toEntity();
        return new SimpleIndexHits<S>(hits, hits.size(), entityType, restAPI);
    }

}
