package org.neo4j.rest.graphdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.neo4j.helpers.collection.MapUtil.map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class BatchRestAPITest extends RestTestBase {
    private RestAPI restAPI;
  
    // TODO transaction check, exception handling if an exception happened in the server

    @Before
    public void init(){
        this.restAPI = ((RestGraphDatabase)getRestGraphDb()).getRestAPI();
    }
    
    @Test
    public void testCreateNode(){
        TestBatchResult response =this.restAPI.executeBatch(new BatchCallback<TestBatchResult>() {
            
            @Override
            public TestBatchResult recordBatch(RestAPI batchRestApi) {
                TestBatchResult result=new TestBatchResult();
                result.n1 = batchRestApi.createNode(map("name", "node1"));
                result.n2 = batchRestApi.createNode(map("name", "node2"));
                return result;
            }
        });     
       
        assertEquals("node1", response.n1.getProperty("name"));
        assertEquals("node2", response.n2.getProperty("name"));
    }
   
    @Test(expected = IllegalStateException.class)
    public void testLeakedBatchApiWontWork() {
        RestAPI leaked =this.restAPI.executeBatch(new BatchCallback<RestAPI>() {
            @Override
            public RestAPI recordBatch(RestAPI batchRestApi) {
                return batchRestApi;
            }
        });
        leaked.createNode(map());
    }

    @Test
    public void testCreateRelationship(){
        TestBatchResult r = this.restAPI.executeBatch(new BatchCallback<TestBatchResult>() {
            @Override
            public TestBatchResult recordBatch(RestAPI batchRestApi) {
                TestBatchResult result=new TestBatchResult();
                result.n1 = batchRestApi.createNode(map("name", "newnode1"));
                result.n2 = batchRestApi.createNode(map("name", "newnode2"));
                result.rel = batchRestApi.createRelationship(result.n1, result.n2, Type.TEST, map("name", "rel") );
                result.allRelationships = result.n1.getRelationships();
                return result;
            }
        });

        Relationship foundRelationship = TestHelper.firstRelationshipBetween( r.n1.getRelationships(Type.TEST, Direction.OUTGOING), r.n1, r.n2);
        Assert.assertNotNull("found relationship", foundRelationship);
        assertEquals("same relationship", r.rel, foundRelationship);
        assertEquals("rel", r.rel.getProperty("name"));

        assertThat(r.n1.getRelationships(Type.TEST, Direction.OUTGOING), new IsRelationshipToNodeMatcher(r.n1, r.n2));
        assertThat(r.n1.getRelationships(Direction.OUTGOING), new IsRelationshipToNodeMatcher(r.n1, r.n2));
        assertThat(r.n1.getRelationships(Direction.BOTH), new IsRelationshipToNodeMatcher(r.n1, r.n2));
        assertThat(r.n1.getRelationships(Type.TEST), new IsRelationshipToNodeMatcher(r.n1, r.n2));
        assertThat(r.allRelationships, new IsRelationshipToNodeMatcher(r.n1, r.n2));
    }
    
    static class TestBatchResult {
        Node n1;
        Node n2;
        RestRelationship rel;
        Iterable<Relationship> allRelationships;
    }
}
