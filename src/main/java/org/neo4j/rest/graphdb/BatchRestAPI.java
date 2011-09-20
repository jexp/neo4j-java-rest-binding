package org.neo4j.rest.graphdb;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.rest.graphdb.ExecutingRestRequest;
import org.neo4j.rest.graphdb.RecordingRestRequest;
import org.neo4j.rest.graphdb.RecordingRestRequest.RestOperation;
import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestNode;
import org.neo4j.rest.graphdb.RestRelationship;
import org.neo4j.rest.graphdb.RestRequest;

public class BatchRestAPI extends RestAPI {

    public BatchRestAPI( String uri ) {
        super(uri);
    }

    public BatchRestAPI( String uri, String user, String password ) {       
        super(uri, user, password); 
    }
    
    public BatchRestAPI(String uri, ExecutingRestRequest executingRestRequest){
        super(uri);
        this.restRequest =  new RecordingRestRequest(executingRestRequest);
    }
    
    @Override
    protected RestRequest createRestRequest( String uri, String user, String password){
        return new RecordingRestRequest(new ExecutingRestRequest(uri,  user,  password));
    }
    
    
    @Override
    public Node createRestNode(RequestResult requestResult) {        
        final long batchId = requestResult.getBatchId();       
        return new RestNode("{"+batchId+"}", this);
    }
       
    @Override
    public RestRelationship createRelationship(Node startNode, Node endNode, RelationshipType type, Map<String, Object> props) {          
        final RestRequest restRequest = ((RestNode)startNode).getRestRequest();
        Map<String, Object> data = MapUtil.map("to", ((RestNode)endNode).getUri(), "type", type.name());
        if (props!=null && props.size()>0) {
            data.put("data",props);
        }          
        RequestResult requestResult = this.restRequest.post(restRequest.getUri()+"/relationships", data); 
        return createRestRelationship(requestResult, startNode);
    }
    
    @Override
    public RestRelationship createRestRelationship(RequestResult requestResult, Node startNode) {          
        final long batchId = requestResult.getBatchId();      
        return new RestRelationship("{"+batchId+"}", this);
    }
    
    public Collection<RestOperation> getRecordedOperations(){
       return ((RecordingRestRequest) this.restRequest).getRecordedRequests();
    }
}
