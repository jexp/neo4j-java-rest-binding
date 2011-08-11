package org.neo4j.rest.graphdb;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.Response.Status;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.index.impl.lucene.LuceneIndexImplementation;
import org.neo4j.rest.graphdb.index.RestIndexManager;


import com.sun.jersey.api.NotFoundException;


public class RestAPI  {
	
	  private final RestRequest restRequest;  
	  private long propertyRefetchTimeInMillis = 1000;

	  
	 
	public RestAPI(RestRequest restRequest){
		  this.restRequest = restRequest;		
	  }
	  
	  public RestAPI( URI uri ) {
	      this(new RestRequest( uri ));	       
	  }

	  public RestAPI( URI uri, String user, String password ) {
	      this(new RestRequest( uri, user, password ));	      
	  }	  
	 

	 
	  public RestIndexManager index() {
	        return new RestIndexManager( restRequest, this);
	  }
	  
	  public Node getNodeById( long id ) {
	    	RequestResult response = restRequest.get( "node/" + id );
	        if ( restRequest.statusIs( response, Status.NOT_FOUND ) ) {
	            throw new NotFoundException( "" + id );
	        }
	        return new RestNode( restRequest.toMap( response ), this );
	  }
	  
	  public Relationship getRelationshipById(long id) {
	        RequestResult requestResult = restRequest.get("relationship/" + id);
	        if ( restRequest.statusIs(requestResult, Status.NOT_FOUND ) ) {
	            throw new NotFoundException( "" + id );
	        }
	        return new RestRelationship( restRequest.toMap(requestResult), this );
	  }
	  	  
	  
	  public Node createNode(Map<String, Object> props) {
	        RequestResult requestResult = restRequest.post("node", JsonHelper.createJsonFrom( props ));
	        if ( restRequest.statusOtherThan(requestResult, Status.CREATED) ) {
	            final int status = requestResult.getStatus();
	            throw new RuntimeException( "" + status);
	        }
	        final URI location = requestResult.getLocation();
	        return new RestNode(location, this );
	  }
	  
	  public RestRelationship createRelationship(Node startNode, Node endNode, RelationshipType type, Map<String, Object> props) {
	        return RestRelationship.create((RestNode)startNode,(RestNode)endNode,type,props);
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
	        Map<?, ?> map = restRequest.toMap( restRequest.get( "" ) );
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


}
