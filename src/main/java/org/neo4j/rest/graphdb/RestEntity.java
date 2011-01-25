package org.neo4j.rest.graphdb;

import com.sun.jersey.api.client.ClientResponse;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.helpers.collection.IterableWrapper;

import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

public class RestEntity implements PropertyContainer {
    private Map<?, ?> structuralData;
    private Map<?, ?> propertyData;
    private long lastTimeFetchedPropertyData;
    private RestGraphDatabase graphDatabase;
    protected RestRequest restRequest;

    public RestEntity( URI uri, RestGraphDatabase graphDatabase ) {
        this( uri.toString(), graphDatabase );
    }

    public RestEntity( String uri, RestGraphDatabase graphDatabase ) {
        this.restRequest = graphDatabase.getRestRequest().with( uri );
        this.graphDatabase = graphDatabase;
    }

    public RestEntity( Map<?, ?> data, RestGraphDatabase graphDatabase ) {
        this.structuralData = data;
        this.graphDatabase = graphDatabase;
        this.propertyData = (Map<?, ?>) data.get( "data" );
        this.lastTimeFetchedPropertyData = System.currentTimeMillis();
        String uri = (String) data.get( "self" );
        this.restRequest = graphDatabase.getRestRequest().with( uri );
    }

    public String getUri() {
        return this.restRequest.getUri().toString();
    }

    Map<?, ?> getStructuralData() {
        if ( this.structuralData == null ) {
            this.structuralData = restRequest.toMap( restRequest.get( "" ) );
        }
        return this.structuralData;
    }

    Map<?, ?> getPropertyData() {
        if ( this.propertyData == null || timeElapsed( this.lastTimeFetchedPropertyData, 1000 ) ) {
            ClientResponse response = restRequest.get( "properties" );
            boolean ok = restRequest.statusIs( response, Status.OK );
            if ( ok ) {
                this.propertyData = restRequest.toMap( response );
            } else {
                this.propertyData = Collections.emptyMap();
            }
            this.lastTimeFetchedPropertyData = System.currentTimeMillis();
        }
        return this.propertyData;
    }

    private boolean timeElapsed( long since, long isItGreaterThanThis ) {
        return System.currentTimeMillis() - since > isItGreaterThanThis;
    }

    public Object getProperty( String key ) {
        Map<?, ?> properties = getPropertyData();
        Object value = properties.get( key );
        if ( value == null ) {
            throw new NotFoundException( "'" + key + "' on " + this );
        }
        return value;
    }

    public Object getProperty( String key, Object defaultValue ) {
        Map<?, ?> properties = getPropertyData();
        Object value = properties.get( key );
        return value != null ? value : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public Iterable<String> getPropertyKeys() {
        return new IterableWrapper( getPropertyData().keySet() ) {
            @Override
            protected String underlyingObjectToObject( Object key ) {
                return key.toString();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public Iterable<Object> getPropertyValues() {
        return (Iterable<Object>) getPropertyData().values();
    }

    public boolean hasProperty( String key ) {
        return getPropertyData().containsKey( key );
    }

    public Object removeProperty( String key ) {
        Object value = getProperty( key, null );
        restRequest.delete( "properties/" + key );
        invalidatePropertyData();
        return value;
    }

    public void setProperty( String key, Object value ) {
        restRequest.put( "properties/" + key, JsonHelper.createJsonFrom( value ) );
        invalidatePropertyData();
    }

    private void invalidatePropertyData() {
        this.propertyData = null;
    }

    static long getEntityId( String uri ) {
        return Long.parseLong( uri.substring( uri.lastIndexOf( '/' ) + 1 ) );
    }

    public long getId() {
        return getEntityId( getUri() );
    }

    public void delete() {
        restRequest.delete( "" );
    }

    @Override
    public int hashCode() {
        return (int) getId();
    }

    @Override
    public boolean equals( Object o ) {
        return getClass().equals( o.getClass() ) && getId() == ( (RestEntity) o ).getId();
    }

    public RestGraphDatabase getGraphDatabase() {
        return graphDatabase;
    }
}
