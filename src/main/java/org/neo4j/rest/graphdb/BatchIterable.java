package org.neo4j.rest.graphdb;

import java.util.Iterator;

/**
 * @author mh
 * @since 21.09.11
 */
public class BatchIterable<T> implements Iterable<T>, UpdatableRestResult<Iterable<T>> {
    private final long batchId;
    private RestAPI restApi;
    private Iterable<T> data;

    public BatchIterable(RequestResult requestResult) {
        batchId = requestResult.getBatchId();
    }

    @Override
    public void updateFrom(Iterable<T> newValue, RestAPI restApi) {
        this.data = newValue;
        this.restApi = restApi;
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }
}
