package org.neo4j.rest.graphdb;

public interface RestResultConverter {
    public boolean canHandle(Object Value);
    public Object convert(Object Value);
    
}
