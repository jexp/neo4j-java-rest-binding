package org.neo4j.rest.graphdb;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * @author mh
 * @since 24.01.11
 */
class IsRelationshipToNodeMatcher extends TypeSafeMatcher<Iterable<Relationship>> {
    private final Node startNode;
    private final Node endNode;

    public IsRelationshipToNodeMatcher( Node startNode, Node endNode ) {
        this.startNode = startNode;
        this.endNode = endNode;
    }

    @Override
    public boolean matchesSafely( Iterable<Relationship> relationships ) {
        return TestHelper.firstRelationshipBetween( relationships, startNode, endNode ) != null;
    }

    public void describeTo( Description description ) {
        description.appendValue( startNode ).appendText( " to " ).appendValue( endNode ).appendText( "not contained in relationships" );
    }
}
