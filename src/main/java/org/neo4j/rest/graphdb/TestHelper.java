package org.neo4j.rest.graphdb;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class TestHelper {

	public static Relationship firstRelationshipBetween( Iterable<Relationship> relationships, final Node startNode, final Node endNode ) {
	    for ( Relationship relationship : relationships ) {
	        if ( relationship.getOtherNode( startNode ).equals( endNode ) ) return relationship;
	    }
	    return null;
	}

}
