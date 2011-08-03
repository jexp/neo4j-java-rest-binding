package org.neo4j.rest.graphdb.index;


import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.rest.graphdb.JsonHelper;
import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.RestEntity;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.RestRequest;

import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * @author mh
 * @since 24.01.11
 */
public abstract class RestIndex<T extends PropertyContainer> implements Index<T> {
    private final RestRequest restRequest;
    private final String indexName;
    protected final RestGraphDatabase restGraphDatabase;

    RestIndex( RestRequest restRequest, String indexName, RestGraphDatabase restGraphDatabase ) {
        this.restRequest = restRequest;
        this.indexName = indexName;
        this.restGraphDatabase = restGraphDatabase;
    }

    public String getName() {
        return indexName;
    }

    private String getTypeName() {
        return getEntityType().getSimpleName().toLowerCase();
    }

    public void add( T entity, String key, Object value ) {
        final RestEntity restEntity = (RestEntity) entity;
        String uri = restEntity.getUri();
        final RequestResult response = restRequest.post(indexPath(key, value), JsonHelper.createJsonFrom(uri));
        if (response.getStatus() != 201) throw new RuntimeException(String.format("Error adding element %d %s %s to index %s", restEntity.getId(), key, value, indexName));
    }

    private String indexPath( String key, Object value ) {
        return "index/" + getTypeName() + "/" + indexName + (key!=null? "/" + RestRequest.encode( key ) :"") + (value!=null ? "/" + RestRequest.encode( value ):"");
    }
    private String queryPath( String key, Object value ) {
        return indexPath(key,null) + "?query="+RestRequest.encode( value );
    }

    public void remove( T entity, String key, Object value ) {
        restRequest.delete( indexPath( key, value ) + "/" + ( (RestEntity) entity ).getId() );

    }

    public void remove(T entity, String key) {
        restRequest.delete( indexPath( key,null ) + "/" + ( (RestEntity) entity ).getId() );
    }

    public void remove(T entity) {
        restRequest.delete( indexPath( null, null) + "/" + ( (RestEntity) entity ).getId() );
    }

    public void delete() {
        restRequest.delete(indexPath(null,null));
    }

    public org.neo4j.graphdb.index.IndexHits<T> get( String key, Object value ) {
    	RequestResult response = restRequest.get( indexPath( key, value ) );
        return handleQueryResults(response);
    }

    public IndexHits<T> query( String key, Object value ) {
    	RequestResult response = restRequest.get( queryPath( key, value ) );
        return handleQueryResults(response);
    }

    private IndexHits<T> handleQueryResults(RequestResult response) {
        if ( restRequest.statusIs( response, Response.Status.OK ) ) {
            Collection hits = (Collection) restRequest.toEntity( response );
            return new SimpleIndexHits<T>( hits, hits.size() );
        } else {
            return new SimpleIndexHits<T>( Collections.emptyList(), 0 );
        }
    }

    protected abstract T createEntity( Map<?, ?> item );

    public org.neo4j.graphdb.index.IndexHits<T> query( Object value ) {
        return query("null",value);
    }

    private class SimpleIndexHits<T extends PropertyContainer> implements IndexHits<T> {
        private Collection<Object> hits;
        private int size;
        private Iterator<Object> iterator;

        public SimpleIndexHits( Collection<Object> hits, int size ) {
            this.hits = hits;
            this.iterator = this.hits.iterator();
            this.size = size;
        }

        public int size() {
            return size;
        }

        public void close() {

        }

        public T getSingle() {
            Iterator<Object> it = hits.iterator();
            return it.hasNext() ? transform( it.next() ) : null;
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
            return transform( value );
        }

        private T transform( Object value ) {
            return (T) createEntity( (Map<?, ?>) value );
        }

        public void remove() {

        }
    }
}
