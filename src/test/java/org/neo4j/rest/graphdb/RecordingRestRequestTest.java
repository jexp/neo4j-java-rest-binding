package org.neo4j.rest.graphdb;

import static org.junit.Assert.*;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

public class RecordingRestRequestTest {
    
    private ExecutingRestRequest exeRequest;
    private RecordingRestRequest recRequest;
    private URI baseUri;
    
    @Before
    public void init(){
        try {
            this.baseUri = new URI("www.test.net");
            this.exeRequest = new ExecutingRestRequest(baseUri);
            this.recRequest = new RecordingRestRequest(exeRequest);
        } catch (URISyntaxException e) {
            
        }
    }
    
    @Test
    public void testCreate() {
        RecordingRestRequest testRequest = new RecordingRestRequest(exeRequest);
        assertEquals(this.baseUri, testRequest.baseUri);
        assertEquals(0, testRequest.getRecordedRequests().size());
        
    }
    
    @Test
    public void testGetWithoutData(){
       RequestResult response = recRequest.get("/node");      
       assertEquals(0, response.getBatchId());
       assertEquals(1, recRequest.getRecordedRequests().size());
    }
    
    @Test
    public void testGetWithData(){
       RequestResult response = recRequest.get("/node","Test");      
       assertEquals(0, response.getBatchId());
       assertEquals(1, recRequest.getRecordedRequests().size());
    }
    
    @Test
    public void testPost(){
       RequestResult response = recRequest.post("/node","Test");      
       assertEquals(0, response.getBatchId());
       assertEquals(1, recRequest.getRecordedRequests().size());
    }
    
    @Test
    public void testPut(){
       RequestResult response = recRequest.put("/node","Test");      
       assertEquals(0, response.getBatchId());
       assertEquals(1, recRequest.getRecordedRequests().size());
    }
    
    @Test
    public void testDelete(){
       RequestResult response = recRequest.delete("/node");      
       assertEquals(0, response.getBatchId());
       assertEquals(1, recRequest.getRecordedRequests().size());
    }
    
    @Test
    public void testMultipleEntries(){
       RequestResult response = recRequest.post("/node","Test");      
       assertEquals(0, response.getBatchId());
       assertEquals(1, recRequest.getRecordedRequests().size());     
       response = recRequest.delete("/node");      
       assertEquals(1, response.getBatchId());
       assertEquals(2, recRequest.getRecordedRequests().size());
       response = recRequest.get("/node","Test");      
       assertEquals(2, response.getBatchId());
       assertEquals(3, recRequest.getRecordedRequests().size());
    }

}
