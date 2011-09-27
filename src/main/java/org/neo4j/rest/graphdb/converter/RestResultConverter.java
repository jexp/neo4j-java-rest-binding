package org.neo4j.rest.graphdb.converter;

import org.neo4j.rest.graphdb.RequestResult;

public interface RestResultConverter {
    public Object convertFromRepresentation(RequestResult value);
    
}
