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

    public BatchRestAPI( URI uri ) {
        super(uri);
    }

    public BatchRestAPI( URI uri, String user, String password ) {       
        super(uri, user, password); 
    }
    
    @Override
    protected RestRequest createRestRequest( URI uri, String user, String password){
        return new RecordingRestRequest(new ExecutingRestRequest(uri,  user,  password));
    }
    
    
    @Override
    public Node createNode(Map<String, Object> props) {
        RequestResult requestResult = restRequest.post("node", props);  
        
        final long batchId = requestResult.getBatchId();
        final String location = requestResult.getLocation().toString();       
        return new RestNode(batchId, location, this );
    }
    
    @Override
    public RestRelationship createRelationship(Node startNode, Node endNode, RelationshipType type, Map<String, Object> props) {
        final RestRequest restRequest = ((RestNode)startNode).getRestRequest();
        Map<String, Object> data = MapUtil.map("to", ((RestNode)endNode).getUri(), "type", type.name());
        if (props!=null && props.size()>0) {
            data.put("data",props);
        }

        RequestResult requestResult = restRequest.post( "relationships", data);       
        final long batchId = requestResult.getBatchId();
        final String location = requestResult.getLocation().toString();
        return new RestRelationship(batchId, location,  ((RestNode)startNode).getRestApi() );    
    }
    
    public Collection<RestOperation> getRecordedOperations(){
       return ((RecordingRestRequest) this.restRequest).getRecordedRequests();
    }
}
