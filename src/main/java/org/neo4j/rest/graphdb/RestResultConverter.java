package org.neo4j.rest.graphdb;

public interface RestResultConverter {
    public boolean canHandle(Object value);
    public Object convertFromRepresentation(Object value);
    
}
