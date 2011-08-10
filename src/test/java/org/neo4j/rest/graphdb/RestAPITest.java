package org.neo4j.rest.graphdb;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.rest.graphdb.index.RestIndexManager;

public class RestAPITest extends RestTestBase {
	
	private RestAPI restAPI;
	
	@Before
	public void init(){
		this.restAPI = ((RestGraphDatabase)getRestGraphDb()).getRestAPI();
	}
	
	@Test
    public void testCreateNodeWithParams() {
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("name", "test");
        Node node = this.restAPI.createNode(props);
        Assert.assertEquals( node, getRestGraphDb().getNodeById( node.getId() ));
        Assert.assertEquals( "test", getRestGraphDb().getNodeById( node.getId()).getProperty("name") );
    }
	
	@Test
    public void testCreateRelationshipWithParams() {
        Node refNode = getRestGraphDb().getReferenceNode();
        Node node = getRestGraphDb().createNode();
        Map<String, Object> props = new HashMap<String, Object>();
		props.put("name", "test");
        Relationship rel = this.restAPI.createRelationship(refNode, node, Type.TEST, props );
        Relationship foundRelationship = TestHelper.firstRelationshipBetween( refNode.getRelationships( Type.TEST, Direction.OUTGOING ), refNode, node );
        Assert.assertNotNull( "found relationship", foundRelationship );
        Assert.assertEquals( "same relationship", rel, foundRelationship );
        Assert.assertThat( refNode.getRelationships( Type.TEST, Direction.OUTGOING ), new IsRelationshipToNodeMatcher( refNode, node ) );
        Assert.assertThat( refNode.getRelationships( Direction.OUTGOING ), new IsRelationshipToNodeMatcher( refNode, node ) );
        Assert.assertThat( refNode.getRelationships( Direction.BOTH ), new IsRelationshipToNodeMatcher( refNode, node ) );
        Assert.assertThat( refNode.getRelationships( Type.TEST ), new IsRelationshipToNodeMatcher( refNode, node ) );
        Assert.assertEquals( "test", rel.getProperty("name") );
    }
	
	@Test (expected = IllegalArgumentException.class)
	public void testForNotCreatedIndex() {
		this.restAPI.getIndex("i do not exist");
	}
	
	@Test
	public void testIndex(){
		IndexManager index = getRestGraphDb().index();
   	    Index<Node> testIndex = index.forNodes("indexName", new HashMap<String, String>());
   	    assertTrue(index.existsForNodes("indexName"));
	}
	
	@Test
	public void testGetIndex(){
		 IndexManager index = getRestGraphDb().index();
    	 Index<Node> testIndex = index.forNodes("indexName", new HashMap<String, String>());
    	 Assert.assertEquals(testIndex.getName(), this.restAPI.getIndex("indexName").getName());
	}
	
	@Test
	public void testCreateRestAPIIndexForNode(){
		Node refNode = getRestGraphDb().getReferenceNode();
		this.restAPI.createIndex(Node.class, "indexName", true);
		IndexManager index = getRestGraphDb().index();
  	    assertTrue(index.existsForNodes("indexName"));
	}
	
	@Test
	public void testCreateRestAPIIndexForRelationship(){
		Node refNode = getRestGraphDb().getReferenceNode();
	    Node node = getRestGraphDb().createNode();
	    Map<String, Object> props = new HashMap<String, Object>();
		props.put("name", "test");
	    Relationship rel = this.restAPI.createRelationship(refNode, node, Type.TEST, props );
		this.restAPI.createIndex(rel.getClass(), "indexName", true);
		IndexManager index = getRestGraphDb().index();
  	    assertTrue(index.existsForNodes("indexName"));
	}
}
