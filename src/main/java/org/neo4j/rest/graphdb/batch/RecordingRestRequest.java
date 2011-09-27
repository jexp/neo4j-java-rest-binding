package org.neo4j.rest.graphdb.batch;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.RestRequest;
import org.neo4j.rest.graphdb.batch.RestOperations.RestOperation;
import org.neo4j.rest.graphdb.batch.RestOperations.RestOperation.Methods;



public class RecordingRestRequest implements RestRequest {    
  
    private final String baseUri;   
    private MediaType contentType;
    private MediaType acceptHeader;   
    private RestRequest restRequest;
    private RestOperations operations;
    private boolean stop;


    public RestOperations getOperations() {
        return operations;
    }

    public RecordingRestRequest( RestRequest restRequest) {       
        this( restRequest.getUri(), MediaType.APPLICATION_JSON_TYPE,  MediaType.APPLICATION_JSON_TYPE );
        this.restRequest = restRequest;       
    }   
    
    public RecordingRestRequest( RestRequest restRequest, RestOperations operations  ) {
       this(restRequest);
       this.operations = operations;
    }   
    
    
    public RecordingRestRequest(String baseUri, MediaType contentType, MediaType acceptHeader) {
        this.baseUri = uriWithoutSlash( baseUri );
        this.contentType = contentType;
        this.acceptHeader = acceptHeader;        
    }

    @Override
    public RequestResult get(String path, Object data) {
       return this.record(Methods.GET, path, data, getBaseUri());
    }

    @Override
    public RequestResult delete(String path) {
        return this.record(Methods.DELETE, path, null, getBaseUri());
    }

    @Override
    public RequestResult post(String path, Object data) {       
        return this.record(Methods.POST, path, data, getBaseUri());
    }

    @Override
    public RequestResult put(String path, Object data) {
        return this.record(Methods.PUT, path, data, getBaseUri());
        
    }

    @Override
    public RestRequest with(String uri) {        
        return new RecordingRestRequest(this.restRequest.with(uri), this.operations);
    }

    @Override
    public String getUri() {
        return getBaseUri();
    }

    @Override
    public RequestResult get(String path) {
        return this.record(Methods.GET, path, null, getBaseUri());
    }    
    
    public RequestResult record(Methods method, String path, Object data, String baseUri){
        if (stop) throw new IllegalStateException("BatchRequest already executed");
        return this.operations.record(method, path, data,baseUri);
    }
    
   
        

    private String uriWithoutSlash( String uri ) {
        String uriString = uri;
        return uriString.endsWith( "/" ) ?  uriString.substring( 0, uriString.length() - 1 )  : uri;
    }

    public static String encode( Object value ) {
        if ( value == null ) return "";
        try {
            return URLEncoder.encode( value.toString(), "utf-8" ).replaceAll( "\\+", "%20" );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( e );
        }
    }    
    
    public Map<Long,RestOperation> getRecordedRequests(){
        return this.operations.getRecordedRequests();
    }

    public void stop() {
        this.stop = true;
    }

    public String getBaseUri() {
        return baseUri;
    }
}

