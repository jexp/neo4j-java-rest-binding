package org.neo4j.rest.graphdb;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.helpers.collection.MapUtil;

public class BatchRestAPITest extends RestTestBase {
    private RestAPI restAPI;
  
    
    @Before
    public void init(){
        this.restAPI = ((RestGraphDatabase)getRestGraphDb()).getRestAPI();
    }
    
    @Test
    public void testCreateNode(){
        TwoNodes response =this.restAPI.executeBatch(new BatchCallback() {
            
            @Override
            public TwoNodes recordBatch(RestAPI batchRestApi) {
                TwoNodes tNodes;
                Node n1 = batchRestApi.createNode(MapUtil.map("name", "node1"));                 
                Node n2 = batchRestApi.createNode(MapUtil.map("name", "node2"));  
                tNodes = new TwoNodes(n1, n2);
                return tNodes;
            }
        });     
        //System.out.println(response.getEntity());     
        Assert.assertEquals( "node1", getRestGraphDb().getNodeById(1).getProperty("name") );     
        Assert.assertEquals( "node2", getRestGraphDb().getNodeById(2).getProperty("name") );     
    }
   
    @Test
    public void testCreateRelationship(){
       // RequestResult response =this.restAPI.executeBatch(new BatchCallback() {
        this.restAPI.executeBatch(new BatchCallback() {
            @Override
            public Void recordBatch(RestAPI batchRestApi) {               
             
                Node n1 = batchRestApi.createNode(MapUtil.map("name", "newnode1"));                 
                Node n2 = batchRestApi.createNode(MapUtil.map("name", "newnode2"));              
                Relationship rel = batchRestApi.createRelationship(n1, n2, Type.TEST, MapUtil.map("name", "rel") );
                return null;
            }
        });      
        //System.out.println(response.getEntity());      
        Node n1 =  getRestGraphDb().getNodeById(3);   
        Node n2 =  getRestGraphDb().getNodeById(4); 
        Relationship rel = n1.getSingleRelationship(Type.TEST, Direction.OUTGOING);
      
        Relationship foundRelationship = TestHelper.firstRelationshipBetween( n1.getRelationships( Type.TEST, Direction.OUTGOING ), n1, n2 );        
        Assert.assertNotNull( "found relationship", foundRelationship );
        Assert.assertEquals( "same relationship", rel, foundRelationship );
        Assert.assertThat( n1.getRelationships( Type.TEST, Direction.OUTGOING ), new IsRelationshipToNodeMatcher( n1, n2 ) );
        Assert.assertThat( n1.getRelationships( Direction.OUTGOING ), new IsRelationshipToNodeMatcher( n1, n2 ) );
        Assert.assertThat( n1.getRelationships( Direction.BOTH ), new IsRelationshipToNodeMatcher( n1, n2 ) );
        Assert.assertThat( n1.getRelationships( Type.TEST ), new IsRelationshipToNodeMatcher( n1, n2 ) );
        Assert.assertEquals( "rel", rel.getProperty("name") );           
    } 
    
    static class TwoNodes{
        Node n1;
        Node n2;
        
        TwoNodes(Node node1, Node node2){
            n1 = node1;
            n2 = node2;
        }
        
        public Node getN1() {
            return n1;
        }

        public Node getN2() {
            return n2;
        }
    }
}
