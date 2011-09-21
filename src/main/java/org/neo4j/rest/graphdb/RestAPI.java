package org.neo4j.rest.graphdb;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response.Status;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.impl.lucene.LuceneIndexImplementation;
import org.neo4j.rest.graphdb.RestOperations.RestOperation;
import org.neo4j.rest.graphdb.BatchRestAPI;
import org.neo4j.rest.graphdb.index.RestIndexManager;


import com.sun.jersey.api.NotFoundException;


public class RestAPI  {
	
	  protected RestRequest restRequest;  
	  private long propertyRefetchTimeInMillis = 1000;
	 
	 
	  public RestAPI(RestRequest restRequest){
		  this.restRequest = restRequest;		
	  }
	  
	  public RestAPI( String uri ) {
	      this.restRequest = createRestRequest(uri, null, null);
	  }

	  public RestAPI( String uri, String user, String password ) {	      
	      this.restRequest = createRestRequest(uri, user, password); 
	  }	  
	 
	  protected RestRequest createRestRequest( String uri, String user, String password){
	      return new ExecutingRestRequest(uri,  user,  password);
	  }
	 
	  public RestIndexManager index() {
	        return new RestIndexManager( restRequest, this);
	  }
	  
	  public Node getNodeById( long id ) {
	    	RequestResult response = restRequest.get( "node/" + id );
	        if ( response.statusIs(Status.NOT_FOUND ) ) {
	            throw new NotFoundException( "" + id );
	        }
	        return new RestNode( response.toMap(), this );
	  }
	  
	  public Relationship getRelationshipById(long id) {
	        RequestResult requestResult = restRequest.get("relationship/" + id);
	        if ( requestResult.statusIs(Status.NOT_FOUND ) ) {
	            throw new NotFoundException( "" + id );
	        }
	        return new RestRelationship( requestResult.toMap(), this );
	  }
	  	  
	  
	  public Node createNode(Map<String, Object> props) {
	        RequestResult requestResult = restRequest.post("node", props);  
	        return createRestNode(requestResult);
	  }
	  
	  public Node createRestNode(RequestResult requestResult){
	      if ( requestResult.statusOtherThan(Status.CREATED) ) {
              final int status = requestResult.getStatus();
              throw new RuntimeException( "" + status);
          }
          final URI location = requestResult.getLocation();
          return new RestNode(location, this );
	  }
	  
	  public RestRelationship createRelationship(Node startNode, Node endNode, RelationshipType type, Map<String, Object> props) {	      
	      final RestRequest restRequest = ((RestNode)startNode).getRestRequest();
	      Map<String, Object> data = MapUtil.map("to", ((RestNode)endNode).getUri(), "type", type.name());
          if (props!=null && props.size()>0) {
              data.put("data",props);
          }         
          RequestResult requestResult = restRequest.post( "relationships", data); 
          return createRestRelationship(requestResult, startNode);
	  }
	  
	  public RestRelationship createRestRelationship(RequestResult requestResult,Node startNode){
	     
          if ( requestResult.statusOtherThan(javax.ws.rs.core.Response.Status.CREATED ) ) {
              final int status = requestResult.getStatus();
              throw new RuntimeException( "" + status);
          }
          final URI location = requestResult.getLocation();
          return new RestRelationship(location, ((RestNode)startNode).getRestApi() );
	  }
	  
	  public <T extends PropertyContainer> Index<T> getIndex(String indexName) {
	        final RestIndexManager index = this.index();
	        if (index.existsForNodes(indexName)) return (Index<T>) index.forNodes(indexName);
	        if (index.existsForRelationships(indexName)) return (Index<T>) index.forRelationships(indexName);
	        throw new IllegalArgumentException("Index "+indexName+" does not yet exist");
	  }
	  
	  public <T extends PropertyContainer> Index<T> createIndex(Class<T> type, String indexName, boolean fullText) {		  
		    Map<String, String> config = fullText ? LuceneIndexImplementation.FULLTEXT_CONFIG : LuceneIndexImplementation.EXACT_CONFIG;
	        if (Node.class.isAssignableFrom(type)) {	        	
	        	return (Index<T>) this.index().forNodes(indexName, config);
	        }
	        if (Relationship.class.isAssignableFrom(type)){
	        	return (Index<T>) this.index().forRelationships(indexName, config);
	        }
	        throw new IllegalArgumentException("Required Node or Relationship types to create index, got "+type);
	  }
	  
	  public RestRequest getRestRequest() {
			return restRequest;
	  }	  
	 
	  
	  public TraversalDescription createTraversalDescription() {
	        return new RestTraversal();
	  }
	  
	  public Node getReferenceNode() {
	        Map<?, ?> map = restRequest.get( "" ).toMap();
	        return new RestNode( (String) map.get( "reference_node" ), this);
	  }
	  
	  public long getPropertyRefetchTimeInMillis() {
	        return propertyRefetchTimeInMillis;
	  }
	  
	  public String getStoreDir() {
	     return restRequest.getUri().toString();
	  }
	  
	 
	  public void setPropertyRefetchTimeInMillis(long propertyRefetchTimeInMillis) {
			this.propertyRefetchTimeInMillis = propertyRefetchTimeInMillis;
	  }
	  
	  
	  public <T> T executeBatch( BatchCallback<T> batchCallback){
	      BatchRestAPI batchRestApi = new BatchRestAPI(this.restRequest.getUri(), (ExecutingRestRequest)this.restRequest);
	      T batchResult = batchCallback.recordBatch(batchRestApi);
	      Map<Long, RestOperation> operations = batchRestApi.getRecordedOperations();
	      RequestResult response = this.restRequest.post("batch", createBatchRequestData(operations));	
	      Map<Long,Object> mappedObjects = convertRequestResultToEntities(response);	     
	      updateRestOperations(operations, mappedObjects);
	      return batchResult;
	  }
	  
	  private Collection<Map<String,Object>> createBatchRequestData(Map<Long, RestOperation> operations){
	        Collection<Map<String,Object>> batch = new ArrayList<Map<String,Object>>();
	        for (RestOperation operation : operations.values()){
	            Map<String,Object> params = new HashMap<String, Object>();
	            params.put("method", operation.getMethod());
	            params.put("to", operation.getUri());
	            if (operation.getData() != null){
	                params.put("body", operation.getData());	                
	            }
	            params.put("id", operation.getBatchId());
	            batch.add(params);	          
	        }	        
	        return batch;
	  }
	  
	  private Map<Long,Object> convertRequestResultToEntities(RequestResult response){
	      Object result = JsonHelper.readJson(response.getEntity());
	      Collection<RestResultConverter> converters = getConverters();
	      //TODO handle errors
	      Collection<Map<String,Object>> responseCollection = (Collection<Map<String,Object>>)result;
	      Map<Long,Object> mappedObjects = new HashMap<Long,Object>(responseCollection.size());
	      for (Map<String,Object> entry : responseCollection) {
	           for (RestResultConverter converter : converters){
	               if (converter.canHandle(entry.get("body"))){
	                   Object entity = converter.convertFromRepresentation((Map<String,Object>)entry.get("body"));            
	                   mappedObjects.put(Long.valueOf((Integer)entry.get("id")), entity);
	                   break;
	               }	              
	           }               
          }	     
	      return mappedObjects;
	  }	 
	  
	  private void updateRestOperations( Map<Long, RestOperation> operations, Map<Long,Object> mappedObjects){	      
	      for (RestOperation operation : operations.values()){
	         operation.updateEntity(mappedObjects.get(operation.getBatchId()));
	      }
	  }
	  
	  private Collection<RestResultConverter> getConverters(){
	      Collection<RestResultConverter> converters = new ArrayList<RestResultConverter>();
	      RestEntityExtractor extr = new RestEntityExtractor(this);
	      converters.add(extr);
	      return converters;
	  }


}
