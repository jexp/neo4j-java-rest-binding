package org.neo4j.rest.graphdb;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import javax.ws.rs.core.MediaType;

import org.neo4j.rest.graphdb.RequestResult;


import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import java.util.concurrent.TimeUnit;

public class ExecutingRestRequest implements RestRequest {

    public static final int CONNECT_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(30);
    public static final int READ_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(30);
    private final URI baseUri;
    private final Client client;

    public ExecutingRestRequest( URI baseUri ) {
        this( baseUri, null, null );
    }

    public ExecutingRestRequest( URI baseUri, String username, String password ) {
        this.baseUri = uriWithoutSlash( baseUri );
        client = createClient();
        addAuthFilter(username, password);

    }

    protected void addAuthFilter(String username, String password) {
        if (username == null) return;
        client.addFilter( new HTTPBasicAuthFilter( username, password ) );
    }

    protected Client createClient() {
        Client client = Client.create();

        client.setConnectTimeout(CONNECT_TIMEOUT);
        client.setReadTimeout(READ_TIMEOUT);

        return client;
    }

    private ExecutingRestRequest( URI uri, Client client ) {
        this.baseUri = uriWithoutSlash( uri );
        this.client = client;
    }

    protected URI uriWithoutSlash( URI uri ) {
        String uriString = uri.toString();
        return uriString.endsWith( "/" ) ? uri( uriString.substring( 0, uriString.length() - 1 ) ) : uri;
    }

    public static String encode( Object value ) {
        if ( value == null ) return "";
        try {
            return URLEncoder.encode( value.toString(), "utf-8" ).replaceAll( "\\+", "%20" );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( e );
        }
    }


    private Builder builder( String path ) {
        WebResource resource = client.resource( uri( pathOrAbsolute( path ) ) );
        return resource.accept( MediaType.APPLICATION_JSON_TYPE );
    }

    private String pathOrAbsolute( String path ) {
        if ( path.startsWith( "http://" ) ) return path;
        return baseUri + "/" + path;
    }

 
    @Override
    public RequestResult get( String path ) {
        return RequestResult.extractFrom(builder(path).get(ClientResponse.class));
    }

 
    @Override
    public RequestResult get( String path, Object data ) {
        Builder builder = builder(path);
        if ( data != null ) {
            builder = builder.entity( JsonHelper.createJsonFrom( data ), MediaType.APPLICATION_JSON_TYPE );
        }
        return RequestResult.extractFrom(builder.get(ClientResponse.class));
    }

  
    @Override
    public RequestResult delete(String path) {
        return RequestResult.extractFrom(builder(path).delete(ClientResponse.class));
    }


    @Override
    public RequestResult post( String path, Object data ) {
        Builder builder = builder( path );
        if ( data != null ) {
            builder = builder.entity( JsonHelper.createJsonFrom( data ), MediaType.APPLICATION_JSON_TYPE );
        }
        return RequestResult.extractFrom(builder.post(ClientResponse.class));
    }

  
    @Override
    public RequestResult put( String path, Object data ) {
        Builder builder = builder( path );
        if ( data != null ) {
            builder = builder.entity( JsonHelper.createJsonFrom( data ), MediaType.APPLICATION_JSON_TYPE );
        }
        final ClientResponse response = builder.put(ClientResponse.class);
        response.close();
        return RequestResult.extractFrom(builder.put(ClientResponse.class));
    }



    @Override
    public RestRequest with( String uri ) {
        return new ExecutingRestRequest( uri( uri ), client );
    }

    private URI uri( String uri ) {
        try {
            return new URI( uri );
        } catch ( URISyntaxException e ) {
            throw new RuntimeException( e );
        }
    }

 
    @Override
    public URI getUri() {
        return baseUri;
    }
}
