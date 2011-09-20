package org.neo4j.rest.graphdb;

public interface BatchCallback<T> {
    T recordBatch(RestAPI batchRestApi);
}
