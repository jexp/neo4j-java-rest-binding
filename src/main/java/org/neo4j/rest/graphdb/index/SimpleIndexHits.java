package org.neo4j.rest.graphdb.index;

import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestEntityExtractor;
import org.neo4j.rest.graphdb.UpdatableRestResult;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author mh
 * @since 22.09.11
 */
public class SimpleIndexHits<T extends PropertyContainer> implements IndexHits<T>, UpdatableRestResult<SimpleIndexHits<T>> {
    private Collection<Object> hits;
    private Class<T> entityType;
    private int size;
    private Iterator<Object> iterator;
    private RestEntityExtractor entityExtractor;

    public SimpleIndexHits(long batchId, Class<T> entityType, final RestAPI restApi) {
        this.entityType = entityType;
        this.entityExtractor = restApi.createExtractor();

    }

    public SimpleIndexHits(Collection<Object> hits, int size, Class<T> entityType, final RestAPI restApi) {
        this.hits = hits;
        this.entityType = entityType;
        this.iterator = this.hits.iterator();
        this.size = size;
        this.entityExtractor = restApi.createExtractor();
    }

    public int size() {
        return size;
    }

    public void close() {

    }

    public T getSingle() {
        Iterator<Object> it = hits.iterator();
        return it.hasNext() ? transform(it.next()) : null;
    }

    public float currentScore() {
        return 0;
    }

    public Iterator<T> iterator() {
        return this;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public T next() {
        Object value = iterator.next();
        return transform(value);
    }

    private T transform(Object value) {
        return (T) entityExtractor.convertFromRepresentation(value);
    }

    public void remove() {

    }

    @Override
    public void updateFrom(SimpleIndexHits<T> newValue, RestAPI restApi) {
        this.hits= newValue.hits;
        this.iterator = this.hits.iterator();
        this.size = newValue.size;
        this.entityExtractor = restApi.createExtractor();
    }
}
