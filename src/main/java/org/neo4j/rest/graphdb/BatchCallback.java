package org.neo4j.rest.graphdb;

public interface BatchCallback {
    void recordBatch(RestAPI batchRestApi);
}
