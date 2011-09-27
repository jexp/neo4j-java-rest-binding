package org.neo4j.rest.graphdb.batch;


import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.rest.graphdb.ExecutingRestRequest;
import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestRequest;
import org.neo4j.rest.graphdb.converter.RelationshipIterableConverter;
import org.neo4j.rest.graphdb.converter.RestEntityExtractor;
import org.neo4j.rest.graphdb.converter.RestIndexHitsConverter;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.entity.RestRelationship;
import org.neo4j.rest.graphdb.index.IndexInfo;
import org.neo4j.rest.graphdb.index.SimpleIndexHits;

import java.util.Map;

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
        (getRecordingRequest()).getOperations().addToRestOperation(batchId, node, new RestEntityExtractor(this));
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
        getRecordingRequest().getOperations().addToRestOperation(batchId, relationship, new RestEntityExtractor(this));
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
        getRecordingRequest().getOperations().addToRestOperation(batchId, result, new RelationshipIterableConverter(this));
        return result;
    }

    public <S extends PropertyContainer> IndexHits<S> queryIndex(String indexPath, Class<S> entityType) {
        RequestResult response = restRequest.get(indexPath);
        final long batchId = response.getBatchId();
        final SimpleIndexHits<S> result = new SimpleIndexHits<S>(batchId, entityType, this);
        getRecordingRequest().getOperations().addToRestOperation(batchId, result, new RestIndexHitsConverter(this,entityType));
        return result;
    }

    public  IndexInfo indexInfo(final String indexType) {
        return new BatchIndexInfo();
    }

    private static class BatchIndexInfo implements IndexInfo {

        @Override
        public boolean checkConfig(String indexName, Map<String, String> config) {
            return true;
        }

        @Override
        public String[] indexNames() {
            return new String[0];
        }

        @Override
        public boolean exists(String indexName) {
            return true;
        }

        @Override
        public Map<String, String> getConfig(String name) {
            return null;
        }
    }
}
