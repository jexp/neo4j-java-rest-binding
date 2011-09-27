package org.neo4j.rest.graphdb.batch;

import java.util.Iterator;

import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.UpdatableRestResult;

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
