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

public class RestBatchAPITest extends RestTestBase {
    private RestAPI restAPI;
  
    
    @Before
    public void init(){
        this.restAPI = ((RestGraphDatabase)getRestGraphDb()).getRestAPI();
    }
    
    @Test
    public void testCreateNode(){
        RequestResult response =this.restAPI.executeBatch(new BatchCallback() {
            
            @Override
            public void recordBatch(RestAPI batchRestApi) {
                Map<String, Object> props = new HashMap<String, Object>();
                props.put("name", "node1");
                Node n1 = batchRestApi.createNode(props);  
                Map<String, Object> props2 = new HashMap<String, Object>();
                props2.put("name", "node2");
                Node n2 = batchRestApi.createNode(props2);  
            }
        });     
        //System.out.println(response.getEntity());  
        convert(response);
        Assert.assertEquals( "node1", getRestGraphDb().getNodeById(1).getProperty("name") );     
        Assert.assertEquals( "node2", getRestGraphDb().getNodeById(2).getProperty("name") );     
    }
    
    @Test
    public void testCreateRelationship(){
        RequestResult response =this.restAPI.executeBatch(new BatchCallback() {
            
            @Override
            public void recordBatch(RestAPI batchRestApi) {
               
                Map<String, Object> props = new HashMap<String, Object>();
                props.put("name", "node1");
                Node n1 = batchRestApi.createNode(props);  
                Map<String, Object> props2 = new HashMap<String, Object>();
                props2.put("name", "node2");
                Node n2 = batchRestApi.createNode(props2);  
                
                Map<String, Object> propsrel = new HashMap<String, Object>();
                propsrel.put("name", "rel");
               
                Relationship rel = batchRestApi.createRelationship(n1, n2, Type.TEST, propsrel );
                
            }
        });      
        System.out.println(response.getEntity());      
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
    
    public Map<String,RestEntity> convert( RequestResult response){
        Map<String,RestEntity> entries = new HashMap<String,RestEntity>(); 
        Map<?,?> result = response.toMap();
        //final RestTableResultExtractor extractor = new RestTableResultExtractor(new RestEntityExtractor(restAPI));
        //final List<Map<String, Object>> data = extractor.extract(result);
       
        return entries;
    }
   

}
