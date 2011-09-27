package org.neo4j.rest.graphdb.batch;

import org.neo4j.rest.graphdb.RestAPI;

public interface BatchCallback<T> {
    T recordBatch(RestAPI batchRestApi);
}
