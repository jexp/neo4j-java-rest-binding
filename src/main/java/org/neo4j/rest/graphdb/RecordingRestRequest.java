package org.neo4j.rest.graphdb;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.neo4j.rest.graphdb.RestOperations.RestOperation;
import org.neo4j.rest.graphdb.RestOperations.RestOperation.Methods;



public class RecordingRestRequest implements RestRequest {    
  
    protected final String baseUri;   
    private MediaType contentType;
    private MediaType acceptHeader;   
    private RestRequest restRequest;
    private RestOperations operations;
   
    
    
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
       return this.record(Methods.GET, path, data);
    }

    @Override
    public RequestResult delete(String path) {
        return this.record(Methods.DELETE, path, null);
    }

    @Override
    public RequestResult post(String path, Object data) {       
        return this.record(Methods.POST, path, data);
    }

    @Override
    public RequestResult put(String path, Object data) {
        return this.record(Methods.PUT, path, data);
        
    }

    @Override
    public RestRequest with(String uri) {        
        return new RecordingRestRequest(this.restRequest.with(uri), this.operations);
    }

    @Override
    public String getUri() {
        return baseUri;
    }

    @Override
    public RequestResult get(String path) {
        return this.record(Methods.GET, path, null);
    }    
    
    public RequestResult record(Methods method, String path, Object data){       
        return this.operations.record(method, path, data);
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
}

