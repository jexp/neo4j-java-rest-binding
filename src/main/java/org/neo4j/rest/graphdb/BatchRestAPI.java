package org.neo4j.rest.graphdb;


import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.rest.graphdb.ExecutingRestRequest;
import org.neo4j.rest.graphdb.RecordingRestRequest;
import org.neo4j.rest.graphdb.RestOperations.RestOperation;
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
        this.restRequest =  new RecordingRestRequest(executingRestRequest, new RestOperations());
    }
    
    @Override
    protected RestRequest createRestRequest( String uri, String user, String password){
        return new RecordingRestRequest(new ExecutingRestRequest(uri,  user,  password),new RestOperations());
    }
    
    
    @Override
    public Node createRestNode(RequestResult requestResult) {        
        final long batchId = requestResult.getBatchId();
        Node node = new RestNode("{"+batchId+"}", this);
        ((RecordingRestRequest)this.restRequest).getOperations().addToRestOperation(batchId, node);
        return node;
    }
       
    @Override
    public RestRelationship createRelationship(Node startNode, Node endNode, RelationshipType type, Map<String, Object> props) {          
        final RestRequest restRequest = ((RestNode)startNode).getRestRequest();
        Map<String, Object> data = MapUtil.map("to", ((RestNode)endNode).getUri(), "type", type.name());
        if (props!=null && props.size()>0) {
            data.put("data",props);
        }          
        RequestResult requestResult = this.restRequest.post(restRequest.getUri()+"/relationships", data); 
        //RequestResult requestResult = restRequest.post( "relationships", data); 
        return createRestRelationship(requestResult, startNode);
    }
    
    @Override
    public RestRelationship createRestRelationship(RequestResult requestResult, Node startNode) {          
        final long batchId = requestResult.getBatchId();
        RestRelationship relationship = new RestRelationship("{"+batchId+"}", this);
        ((RecordingRestRequest)this.restRequest).getOperations().addToRestOperation(batchId, relationship);        
        return relationship;
    }
    
    public Map<Long,RestOperation> getRecordedOperations(){
       return ((RecordingRestRequest) this.restRequest).getOperations().getRecordedRequests();
    }
}
