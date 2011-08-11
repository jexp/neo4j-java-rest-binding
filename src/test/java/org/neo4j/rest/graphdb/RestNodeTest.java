package org.neo4j.rest.graphdb;

import static org.junit.Assert.*;
import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;
import static org.neo4j.rest.graphdb.RelationshipHasMatcher.match;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.rest.graphdb.MatrixDataGraph.RelTypes;

public class RestNodeTest extends RestTestBase  {
	
	
	private MatrixDataGraph embeddedMatrixdata;
	private MatrixDataGraph restMatrixData;
	private Node neo;
	

	@Before
	public void createMatrixdata() {
		embeddedMatrixdata = new MatrixDataGraph(getGraphDatabase()).createNodespace();
		restMatrixData = new MatrixDataGraph(getRestGraphDb());
		neo = restMatrixData.getNeoNode();
	}
	
	@Test
	public void testGetRelationshipsWithoutDirectionWithoutRelationshipType() {		
		Iterable<Relationship> relationships = neo.getRelationships();		
		assertThat(relationships, match(neo, null));		
	}
	
	@Test
	public void testGetRelationshipsWithIncomingDirectionWithoutRelationshipType() {		
		Iterable<Relationship> relationships = neo.getRelationships(INCOMING);		
		assertThat(relationships, match(neo, INCOMING));		
	}
	
	@Test
	public void testGetRelationshipsWithOutgoingDirectionWithoutRelationshipType() {		
		Iterable<Relationship> relationships = neo.getRelationships(OUTGOING);		
		assertThat(relationships, match(neo, OUTGOING));		
	}
	
	@Test
	public void testGetRelationshipsWithoutDirectionWithSingleRelationshipType() {		
		Iterable<Relationship> relationships = neo.getRelationships(RelTypes.NEO_NODE);		
		assertThat(relationships, match(neo, null, RelTypes.NEO_NODE));		
	}	
	
	
	@Test
	public void testGetRelationshipsWithIncomingDirectionWithSingleRelationshipType() {		
		Iterable<Relationship> relationships = neo.getRelationships(INCOMING, RelTypes.NEO_NODE);		
		assertThat(relationships, match(neo, INCOMING, RelTypes.NEO_NODE));		
	}
	
	@Test
	public void testGetRelationshipsWithOutgoingDirectionWithSingleRelationshipType() {		
		Iterable<Relationship> relationships = neo.getRelationships(OUTGOING, RelTypes.KNOWS);		
		assertThat(relationships, match(neo, OUTGOING, RelTypes.KNOWS));		
	}
	
	@Test
	public void testGetRelationshipsWithoutDirectionWithMultipleRelationshipTypes() {		
		Iterable<Relationship> relationships = neo.getRelationships(RelTypes.NEO_NODE, RelTypes.HERO);		
		assertThat(relationships, match(neo, null, RelTypes.NEO_NODE, RelTypes.HERO));		
	}
	
	@Test
	public void testGetRelationshipsWithIncomingDirectionWithMultipleRelationshipTypes() {		
		Iterable<Relationship> relationships = neo.getRelationships(INCOMING, RelTypes.NEO_NODE, RelTypes.HERO );		
		assertThat(relationships, match(neo, INCOMING, RelTypes.NEO_NODE, RelTypes.HERO));
		
	}
	
	@Test
	public void testGetRelationshipsWithOutgoingDirectionWithMultipleRelationshipTypes() {		
		Iterable<Relationship> relationships = neo.getRelationships(OUTGOING, RelTypes.KNOWS, RelTypes.FIGHTS );		
		assertThat(relationships, match(neo, OUTGOING, RelTypes.KNOWS, RelTypes.FIGHTS));
		
	}
	
	@Test
	public void testHasRelationshipsWithoutDirectionWithoutRelationshipType() {		
		boolean hasRelationship = neo.hasRelationship();		
		assertTrue(hasRelationship);		
	}
	
	@Test
	public void testHasRelationshipsWithIncomingDirectionWithoutRelationshipType() {		
		boolean hasRelationship = neo.hasRelationship(INCOMING);		
		assertTrue(hasRelationship);		
	}
	
	@Test
	public void testHasRelationshipsWithOutgoingDirectionWithoutRelationshipType() {		
		boolean hasRelationship = neo.hasRelationship(OUTGOING);		
		assertTrue(hasRelationship);		
	}
	
	@Test
	public void testHasRelationshipsWithoutDirectionWithSingleRelationshipType() {		
		boolean hasRelationship = neo.hasRelationship(RelTypes.KNOWS);		
		assertTrue(hasRelationship);		
	}
	
	@Test
	public void testHasRelationshipsWithIncomingDirectionWithSingleRelationshipType() {		
		boolean hasRelationship = neo.hasRelationship(INCOMING, RelTypes.HERO);		
		assertTrue(hasRelationship);		
	}
	
	@Test
	public void testHasRelationshipsWithIncomingDirectionWithSingleRelationshipTypeParamsReversed() {		
		boolean hasRelationship = neo.hasRelationship(RelTypes.HERO, INCOMING);		
		assertTrue(hasRelationship);		
	}
	
	@Test
	public void testHasRelationshipsWithOutgoingDirectionWithSingleRelationshipType() {		
		boolean hasRelationship = neo.hasRelationship(OUTGOING, RelTypes.KNOWS);		
		assertTrue(hasRelationship);		
	}
	
	@Test
	public void testHasRelationshipsWithOutgoingDirectionWithSingleRelationshipTypeParamsReversed() {		
		boolean hasRelationship = neo.hasRelationship(RelTypes.KNOWS, OUTGOING);		
		assertTrue(hasRelationship);		
	}
	
	@Test
	public void testHasRelationshipsWithoutDirectionWithMultipleRelationshipTypes() {		
		boolean hasRelationship = neo.hasRelationship(RelTypes.KNOWS, RelTypes.HERO);		
		assertTrue(hasRelationship);		
	}
	
	@Test
	public void testHasRelationshipsWithIncomingDirectionWithMultipleRelationshipTypes() {		
		boolean hasRelationship = neo.hasRelationship(INCOMING, RelTypes.NEO_NODE, RelTypes.HERO);		
		assertTrue(hasRelationship);		
	}
	
	@Test
	public void testHasRelationshipsWithOutgoingDirectionWithMultipleRelationshipTypes() {		
		boolean hasRelationship = neo.hasRelationship(OUTGOING, RelTypes.KNOWS, RelTypes.FIGHTS);		
		assertTrue(hasRelationship);		
	}

}
