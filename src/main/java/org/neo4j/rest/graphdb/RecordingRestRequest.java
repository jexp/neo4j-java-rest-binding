package org.neo4j.rest.graphdb;


import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.neo4j.rest.graphdb.RecordingRestRequest.RestOperation.Methods;



public class RecordingRestRequest implements RestRequest {
    
    private Collection<RestOperation> operations = new ArrayList<RecordingRestRequest.RestOperation>();
    protected final URI baseUri;   
    private MediaType contentType;
    private MediaType acceptHeader;
    private long currentBatchId = 0;
    private ExecutingRestRequest executingRestRequest;
   
    
    
    public RecordingRestRequest( ExecutingRestRequest restRequest ) {
        this( restRequest.getUri(), MediaType.APPLICATION_JSON_TYPE,  MediaType.APPLICATION_JSON_TYPE );
        this.executingRestRequest = restRequest;
    }   
    
    
    public RecordingRestRequest(URI baseUri, MediaType contentType, MediaType acceptHeader) {
        this.baseUri = uriWithoutSlash( baseUri );
        this.contentType = contentType;
        this.acceptHeader = acceptHeader;        
        
    }   
       

    public static class RestOperation { 
        public enum Methods{
            POST,
            PUT,
            GET,
            DELETE
        }
        
        private Methods method;
        private Object data;
        private long batchId;
        private String uri;
        private MediaType contentType;
        private MediaType acceptHeader;
        
        public RestOperation(long batchId, Methods method, String uri, MediaType contentType, MediaType acceptHeader, Object data){
            this.batchId = batchId;
            this.method = method;
            this.uri = uri;
            this.contentType = contentType;
            this.acceptHeader = acceptHeader;
            this.data = data;
        }
        
        public Methods getMethod() {
            return method;
        }

        public Object getData() {
            return data;
        }

        public long getBatchId() {
            return batchId;
        }

        public String getUri() {
            return uri;
        }

        public MediaType getContentType() {
            return contentType;
        }

        public MediaType getAcceptHeader() {
            return acceptHeader;
        }
          
    }
    
    public Collection<RestOperation> getRecordedRequests(){
        return this.operations;
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
        return new RecordingRestRequest((ExecutingRestRequest)this.executingRestRequest.with(uri));
    }

    @Override
    public URI getUri() {
        return baseUri;
    }

    @Override
    public RequestResult get(String path) {
        return this.record(Methods.GET, path, null);
    }    
    
    public RequestResult record(Methods method, String path, Object data){
        RestOperation r = new RestOperation(this.currentBatchId++,method,path,this.contentType,this.acceptHeader,data);
        operations.add(r);
        return RequestResult.batchResult(r);
    }
      
    
    private URI uri( String uri ) {
        try {
            return new URI( uri );
        } catch ( URISyntaxException e ) {
            throw new RuntimeException( e );
        }
    }   

    private URI uriWithoutSlash( URI uri ) {
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
      
}

