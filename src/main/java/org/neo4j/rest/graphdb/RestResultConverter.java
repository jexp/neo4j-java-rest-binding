package org.neo4j.rest.graphdb;

public interface RestResultConverter {
    public Object convertFromRepresentation(RequestResult value);
    
}
