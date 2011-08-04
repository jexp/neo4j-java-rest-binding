package org.neo4j.rest.graphdb;

import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphdb.*;

import java.util.Date;

public class RestGraphDbTest extends RestTestBase {

    @Test
    public void testGetRefNode() {
        Node refNode = getRestGraphDb().getReferenceNode();
        Node nodeById = getRestGraphDb().getNodeById( 0 );
        Assert.assertEquals( refNode, nodeById );
    }

    @Test
    public void testCreateNode() {
        Node node = getRestGraphDb().createNode();
        Assert.assertEquals( node, getRestGraphDb().getNodeById( node.getId() ) );
    }

    @Test
    public void testCreateRelationship() {
        Node refNode = getRestGraphDb().getReferenceNode();
        Node node = getRestGraphDb().createNode();
        Relationship rel = refNode.createRelationshipTo( node, Type.TEST );
        Relationship foundRelationship = IsRelationshipToNodeMatcher.relationshipFromTo( refNode.getRelationships( Type.TEST, Direction.OUTGOING ), refNode, node );
        Assert.assertNotNull( "found relationship", foundRelationship );
        Assert.assertEquals( "same relationship", rel, foundRelationship );
        Assert.assertThat( refNode.getRelationships( Type.TEST, Direction.OUTGOING ), new IsRelationshipToNodeMatcher( refNode, node ) );
        Assert.assertThat( refNode.getRelationships( Direction.OUTGOING ), new IsRelationshipToNodeMatcher( refNode, node ) );
        Assert.assertThat( refNode.getRelationships( Direction.BOTH ), new IsRelationshipToNodeMatcher( refNode, node ) );
        Assert.assertThat( refNode.getRelationships( Type.TEST ), new IsRelationshipToNodeMatcher( refNode, node ) );
    }

    @Test
    public void testBasic() {
        Node refNode = getRestGraphDb().getReferenceNode();
        Node node = getRestGraphDb().createNode();
        Relationship rel = refNode.createRelationshipTo( node,
                DynamicRelationshipType.withName( "TEST" ) );
        rel.setProperty( "date", new Date().getTime() );
        node.setProperty( "name", "Mattias test" );
        refNode.createRelationshipTo( node,
                DynamicRelationshipType.withName( "TEST" ) );

        for ( Relationship relationship : refNode.getRelationships() ) {
            System.out.println( "rel prop:" + relationship.getProperty( "date", null ) );
            Node endNode = relationship.getEndNode();
            System.out.println( "node prop:" + endNode.getProperty( "name", null ) );
        }
    }

}
