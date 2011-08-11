package org.neo4j.rest.graphdb;


import org.neo4j.graphdb.*;
import org.neo4j.graphdb.event.KernelEventHandler;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.kernel.Config;
import org.neo4j.kernel.RestConfig;
import org.neo4j.rest.graphdb.index.RestIndexManager;
import java.io.Serializable;
import java.net.URI;
import java.util.Map;

public class RestGraphDatabase extends AbstractGraphDatabase {   
    private RestAPI restAPI;

    
    public RestGraphDatabase( RestAPI api){
    	this.restAPI = api;    	
    }
    
    public RestGraphDatabase( URI uri ) {     
        this( new RestRequest( uri ));
    }

    public RestGraphDatabase( URI uri, String user, String password ) {        
        this(new RestRequest( uri, user, password ));
    }
    
    public RestGraphDatabase( RestRequest restRequest){
    	this(new RestAPI(restRequest)); 	
    }
    
    
    
    
    public RestAPI getRestAPI(){
    	return this.restAPI;
    }
    
    public Transaction beginTx() {
        return new Transaction() {
            public void success() {
            }

            public void finish() {

            }

            public void failure() {
            }
        };
    }

    public <T> TransactionEventHandler<T> registerTransactionEventHandler( TransactionEventHandler<T> tTransactionEventHandler ) {
        throw new UnsupportedOperationException();
    }

    public <T> TransactionEventHandler<T> unregisterTransactionEventHandler( TransactionEventHandler<T> tTransactionEventHandler ) {
        throw new UnsupportedOperationException();
    }

    public KernelEventHandler registerKernelEventHandler( KernelEventHandler kernelEventHandler ) {
        throw new UnsupportedOperationException();
    }

    public KernelEventHandler unregisterKernelEventHandler( KernelEventHandler kernelEventHandler ) {
        throw new UnsupportedOperationException();
    }

    public RestIndexManager index() {
       return this.restAPI.index();
    }

    public Node createNode() {
    	return this.restAPI.createNode(null);
    }
       

    public boolean enableRemoteShell() {
        throw new UnsupportedOperationException();
    }

    public boolean enableRemoteShell( Map<String, Serializable> config ) {
        throw new UnsupportedOperationException();
    }

    public Iterable<Node> getAllNodes() {
        throw new UnsupportedOperationException();
    }
  
    public Iterable<RelationshipType> getRelationshipTypes() {
        throw new UnsupportedOperationException();
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

    public void shutdown() {
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
    public <T> T getManagementBean(Class<T> type) {
        return null;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}
