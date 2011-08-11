package org.neo4j.rest.graphdb;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.Response.Status;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
import org.neo4j.index.impl.lucene.LuceneIndexImplementation;
import org.neo4j.rest.graphdb.index.RestIndexManager;


public class RestAPI  {
	
	  private RestRequest restRequest;
	  @Deprecated
	  private RestGraphDatabase restGraphDatabase;
	  
	  public RestAPI(RestRequest restRequest, RestGraphDatabase restGraphDatabase ){
		  this.restRequest = restRequest;
		  this.restGraphDatabase = restGraphDatabase;
	  }
	  
	 	  
	  public Node createNode(Map<String, Object> props) {
	        RequestResult requestResult = restRequest.post("node", JsonHelper.createJsonFrom( props ));
	        if ( restRequest.statusOtherThan(requestResult, Status.CREATED) ) {
	            final int status = requestResult.getStatus();
	            throw new RuntimeException( "" + status);
	        }
	        final URI location = requestResult.getLocation();
	        return new RestNode(location, this.restGraphDatabase );
	  }
	  
	  public Relationship createRelationship(Node startNode, Node endNode, RelationshipType type, Map<String, Object> props) {
	        return RestRelationship.create((RestNode)startNode,(RestNode)endNode,type,props);
	  }
	  
	  
	  public <T extends PropertyContainer> Index<T> getIndex(String indexName) {
	        final RestIndexManager index = this.restGraphDatabase.index();
	        if (index.existsForNodes(indexName)) return (Index<T>) index.forNodes(indexName);
	        if (index.existsForRelationships(indexName)) return (Index<T>) index.forRelationships(indexName);
	        throw new IllegalArgumentException("Index "+indexName+" does not yet exist");
	  }
	  
	  public <T extends PropertyContainer> Index<T> createIndex(Class<T> type, String indexName, boolean fullText) {		  
		    Map<String, String> config = fullText ? LuceneIndexImplementation.FULLTEXT_CONFIG : LuceneIndexImplementation.EXACT_CONFIG;
	        if (Node.class.isAssignableFrom(type)) {	        	
	        	return (Index<T>) this.restGraphDatabase.index().forNodes(indexName, config);
	        }
	        if (Relationship.class.isAssignableFrom(type)){
	        	return (Index<T>) this.restGraphDatabase.index().forRelationships(indexName, config);
	        }
	        throw new IllegalArgumentException("Required Node or Relationship types to create index, got "+type);
	  }


}
