package org.neo4j.rest.graphdb;

import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.util.Arrays;

public class TestRestEntity extends RestTestBase {

    @Test
    public void testSetProperty() {
        RestTestBase.graphDb.getReferenceNode().setProperty( "name", "test" );
        Node node = RestTestBase.graphDb.getReferenceNode();
        Assert.assertEquals( "test", node.getProperty( "name" ) );
    }

    @Test
    public void testSetStringArrayProperty() {
        RestTestBase.graphDb.getReferenceNode().setProperty( "name", new String[]{"test"} );
        Node node = RestTestBase.graphDb.getReferenceNode();
        Assert.assertArrayEquals( new String[]{"test"}, (String[])node.getProperty( "name" ) );
    }
    @Test
    public void testSetDoubleArrayProperty() {
        double[] data = {0, 1, 2};
        RestTestBase.graphDb.getReferenceNode().setProperty( "data", data );
        Node node = RestTestBase.graphDb.getReferenceNode();
        Assert.assertTrue("same double array",Arrays.equals( data, (double[])node.getProperty( "data" ) ));
    }

    @Test
    public void testRemoveProperty() {
        Node node = RestTestBase.graphDb.getReferenceNode();
        node.setProperty( "name", "test" );
        Assert.assertEquals( "test", node.getProperty( "name" ) );
        node.removeProperty( "name" );
        Assert.assertEquals( false, node.hasProperty( "name" ) );
    }


    @Test
    public void testSetPropertyOnRelationship() {
        Node refNode = RestTestBase.graphDb.getReferenceNode();
        Node node = RestTestBase.graphDb.createNode();
        Relationship rel = refNode.createRelationshipTo( node, Type.TEST );
        rel.setProperty( "name", "test" );
        Assert.assertEquals( "test", rel.getProperty( "name" ) );
        Relationship foundRelationship = IsRelationshipToNodeMatcher.relationshipFromTo( refNode.getRelationships( Type.TEST, Direction.OUTGOING ), refNode, node );
        Assert.assertEquals( "test", foundRelationship.getProperty( "name" ) );
    }

    @Test
    public void testRemovePropertyOnRelationship() {
        Node refNode = RestTestBase.graphDb.getReferenceNode();
        Node node = RestTestBase.graphDb.createNode();
        Relationship rel = refNode.createRelationshipTo( node, Type.TEST );
        rel.setProperty( "name", "test" );
        Assert.assertEquals( "test", rel.getProperty( "name" ) );
        Relationship foundRelationship = IsRelationshipToNodeMatcher.relationshipFromTo( refNode.getRelationships( Type.TEST, Direction.OUTGOING ), refNode, node );
        Assert.assertEquals( "test", foundRelationship.getProperty( "name" ) );
        rel.removeProperty( "name" );
        Assert.assertEquals( false, rel.hasProperty( "name" ) );
        Relationship foundRelationship2 = IsRelationshipToNodeMatcher.relationshipFromTo( refNode.getRelationships( Type.TEST, Direction.OUTGOING ), refNode, node );
        Assert.assertEquals( false, foundRelationship2.hasProperty( "name" ) );
    }

}
