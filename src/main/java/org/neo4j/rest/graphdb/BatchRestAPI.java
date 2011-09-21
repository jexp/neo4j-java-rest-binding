package org.neo4j.rest.graphdb;


import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.MapUtil;

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
        (getRecordingRequest()).getOperations().addToRestOperation(batchId, node);
        return node;
    }
       
    @Override
    public RestRelationship createRelationship(Node startNode, Node endNode, RelationshipType type, Map<String, Object> props) {          
        final RestRequest restRequest = ((RestNode)startNode).getRestRequest();
        Map<String, Object> data = MapUtil.map("to", ((RestNode)endNode).getUri(), "type", type.name());
        if (props!=null && props.size()>0) {
            data.put("data",props);
        }          
        //RequestResult requestResult = restRequest.post(restRequest.getUri()+"/relationships", data);
        RequestResult requestResult = restRequest.post( "relationships", data);
        return createRestRelationship(requestResult, startNode);
    }
    
    @Override
    public RestRelationship createRestRelationship(RequestResult requestResult, Node startNode) {          
        final long batchId = requestResult.getBatchId();
        RestRelationship relationship = new RestRelationship("{"+batchId+"}", this);
        getRecordingRequest().getOperations().addToRestOperation(batchId, relationship);
        return relationship;
    }

    private RecordingRestRequest getRecordingRequest() {
        return (RecordingRestRequest)this.restRequest;
    }

    public RestOperations getRecordedOperations(){
       return (getRecordingRequest()).getOperations();
    }

    public void stop() {
        getRecordingRequest().stop();
    }

    @SuppressWarnings("unchecked")
    public Iterable<Relationship> wrapRelationships(  RequestResult requestResult ) {
        final long batchId = requestResult.getBatchId();
        final BatchIterable<Relationship> result = new BatchIterable<Relationship>(requestResult);
        getRecordingRequest().getOperations().addToRestOperation(batchId, result);
        return result;
    }

}
