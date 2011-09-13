package org.neo4j.rest.graphdb.index;

import com.sun.jersey.api.client.ClientResponse;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipAutoIndexer;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.index.impl.lucene.LuceneIndexImplementation;
import org.neo4j.rest.graphdb.JsonHelper;
import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.RestRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RestIndexManager implements IndexManager {
    public static final String RELATIONSHIP = "relationship";
    public static final String NODE = "node";
    private RestRequest restRequest;
    private RestAPI restApi;

    public RestIndexManager( RestRequest restRequest, RestAPI restApi ) {
        this.restRequest = restRequest;
        this.restApi = restApi;
    }

    public boolean existsForNodes( String indexName ) {
        return indexInfo(NODE).containsKey( indexName );
    }

    @SuppressWarnings({"unchecked"})
    private Map<String, ?> indexInfo( final String indexType ) {
    	RequestResult response = restRequest.get( "index/" + indexType );
        if ( response.statusIs(ClientResponse.Status.NO_CONTENT ) ) return Collections.emptyMap();
        return (Map<String, ?>) response.toMap();
    }
    
    @SuppressWarnings("unchecked")
	private boolean checkIndex(  final String indexType, final String indexName, Map<String, String> config ){
    	Map<String, String> existingConfig = (Map<String, String>) indexInfo(indexType).get(indexName);
    	if (config == null){
    		return existingConfig!=null;
    	}else {
    		if (existingConfig == null){
    			return false;
    		}else{      			
    			if (existingConfig.entrySet().containsAll(config.entrySet())){    				
    				return true;
    			}else{
    				throw new IllegalArgumentException("Index with the same name but different config exists!");
    			}    			
    		}    		
    	}    	
    }
    
    public boolean noConfigProvided(Map<String,String> config) { 
    	return config == null || config.isEmpty();
    }

    public Index<Node> forNodes( String indexName ) {
    	if (!checkIndex(NODE, indexName, null)){    		
    		createIndex(NODE, indexName,  LuceneIndexImplementation.EXACT_CONFIG);
    	}
        return new RestNodeIndex( restRequest, indexName, restApi );
    }

    public Index<Node> forNodes( String indexName, Map<String, String> config ) { 
    	if (noConfigProvided(config)){
    		throw new IllegalArgumentException("No index configuration was provided!");
    	}
    	if (!checkIndex(NODE, indexName, config)){
    		createIndex(NODE, indexName, config);
    	}    	
        return new RestNodeIndex( restRequest, indexName, restApi );
    }

    public String[] nodeIndexNames() {
        Set<String> keys = indexInfo(NODE).keySet();
        return keys.toArray( new String[keys.size()] );
    }

    public boolean existsForRelationships( String indexName ) {
        return indexInfo(RELATIONSHIP).containsKey( indexName );
    }

    public RelationshipIndex forRelationships( String indexName ) {
    	if (!checkIndex(RELATIONSHIP, indexName, null)){    		
    		createIndex(RELATIONSHIP, indexName,  LuceneIndexImplementation.EXACT_CONFIG);
    	}
        return new RestRelationshipIndex( restRequest, indexName, restApi );
    }

    public RelationshipIndex forRelationships( String indexName, Map<String, String> config ) {
    	if (noConfigProvided(config)){
    		throw new IllegalArgumentException("No index configuration was provided!");
    	}
    	if (!checkIndex(RELATIONSHIP, indexName, config)){
    		createIndex(RELATIONSHIP, indexName, config);
    	}    
        return new RestRelationshipIndex( restRequest, indexName, restApi );
    }

    private void createIndex(String type, String indexName, Map<String, String> config) {
        Map<String,Object> data=new HashMap<String, Object>();
        data.put("name",indexName);
        data.put("config",config);
        restRequest.post("index/" + type, data);
    }

    public String[] relationshipIndexNames() {
        Set<String> keys = indexInfo(RELATIONSHIP).keySet();
        return keys.toArray( new String[keys.size()] );
    }

    @SuppressWarnings({"unchecked"})
    public Map<String, String> getConfiguration( Index<? extends PropertyContainer> index ) {
        String typeName = typeName(index.getEntityType());
        return (Map<String, String>) indexInfo(typeName).get(index.getName());
    }

    private String typeName(Class<? extends PropertyContainer> type) {
        if (Node.class.isAssignableFrom(type)) return NODE;
        if (Relationship.class.isAssignableFrom(type)) return RELATIONSHIP;
        throw new IllegalArgumentException("Invalid index type "+type);
    }

    public String setConfiguration( Index<? extends PropertyContainer> index, String s, String s1 ) {
        throw new UnsupportedOperationException();
    }

    public String removeConfiguration( Index<? extends PropertyContainer> index, String s ) {
        throw new UnsupportedOperationException();
    }

	@Override
	public AutoIndexer<Node> getNodeAutoIndexer() {
		 throw new UnsupportedOperationException();
	}

	@Override
	public RelationshipAutoIndexer getRelationshipAutoIndexer() {
		 throw new UnsupportedOperationException();
	}
}

