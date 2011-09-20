package org.neo4j.rest.graphdb;


import org.neo4j.graphdb.*;
import org.neo4j.kernel.Config;
import org.neo4j.kernel.RestConfig;
import org.neo4j.rest.graphdb.index.RestIndexManager;
import java.net.URI;


public class RestGraphDatabase extends AbstractRemoteDatabase {   
    private RestAPI restAPI;

    
    public RestGraphDatabase( RestAPI api){
    	this.restAPI = api;    	
    }
    
    public RestGraphDatabase( String uri ) {     
        this( new ExecutingRestRequest( uri ));
    }

    public RestGraphDatabase( String uri, String user, String password ) {        
        this(new ExecutingRestRequest( uri, user, password ));
    }
    
    public RestGraphDatabase( RestRequest restRequest){
    	this(new RestAPI(restRequest)); 	
    } 
    
    
    public RestAPI getRestAPI(){
    	return this.restAPI;
    }
    
    
    public RestIndexManager index() {
       return this.restAPI.index();
    }

    public Node createNode() {
    	return this.restAPI.createNode(null);
    }
  
    public Node getNodeById( long id ) {
    	return this.restAPI.getNodeById(id);
    }

    public Node getReferenceNode() {
        return this.restAPI.getReferenceNode();
    }

    public Relationship getRelationshipById( long id ) {
    	return this.restAPI.getRelationshipById(id);
    }    

  
    public RestRequest getRestRequest() {
        return this.restAPI.getRestRequest();
    }

    public long getPropertyRefetchTimeInMillis() {
        return this.restAPI.getPropertyRefetchTimeInMillis();
	}
    @Override
    public String getStoreDir() {
        return this.restAPI.getStoreDir();
    }

    @Override
    public Config getConfig() {
        return new RestConfig(this);
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}
