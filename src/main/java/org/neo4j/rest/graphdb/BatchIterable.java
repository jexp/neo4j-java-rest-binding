package org.neo4j.rest.graphdb;

import org.neo4j.helpers.collection.IterableWrapper;

import java.util.Iterator;
import java.util.Map;

/**
 * @author mh
 * @since 21.09.11
 */
public class BatchIterable<T> implements Iterable<T>, UpdatableRestResult {
    private final long batchId;
    private RestAPI restApi;
    private Iterable<T> data;

    public BatchIterable(RequestResult requestResult) {
        batchId = requestResult.getBatchId();
    }

    @Override
    public void updateFrom(Object newValue, RestAPI restApi) {
        this.data = (Iterable<T>) newValue;
        this.restApi = restApi;
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }
}
