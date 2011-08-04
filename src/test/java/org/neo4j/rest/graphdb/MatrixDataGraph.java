package org.neo4j.rest.graphdb;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;


/**
 * Creates a database using the matrix example for testing purposes
 * @author Klemens Burchardi
 * @since 03.08.11
 */
public class MatrixDataGraph {
	 /** specify relationship types*/
	 public enum RelTypes implements RelationshipType{
	     NEO_NODE,
	     KNOWS,
	     CODED_BY
	 }	 
	
	 private GraphDatabaseService graphDb;
	 
	 public MatrixDataGraph(GraphDatabaseService graphDb){
		 this.graphDb = graphDb;
		 createNodespace();
	 }
		
	 
	 /**
	  * fills the database with nodes and relationships, using the matrix example
	  * @param graphDb the graph database to fill 
	  */
	 private void createNodespace() {
	      Transaction tx = this.graphDb.beginTx();
	      try {
	    	 // create neo node
	         Node neo = this.graphDb.createNode();
	         neo.setProperty( "age", 29 );
	         neo.setProperty( "name", "Thomas Anderson" );	         
	         // connect neo to the reference node
	         Node referenceNode = this.graphDb.getReferenceNode();
	         referenceNode.createRelationshipTo( neo, RelTypes.NEO_NODE );
	         // create trinity node
	         Node trinity = this.graphDb.createNode();
	         trinity.setProperty( "name", "Trinity" );
	         Relationship rel = neo.createRelationshipTo( trinity, RelTypes.KNOWS );
	         rel.setProperty( "age", "3 days" );
	         // create morpheus node
	         Node morpheus = this.graphDb.createNode();
	         morpheus.setProperty( "name", "Morpheus" );
	         morpheus.setProperty( "occupation", "Total badass" );
	         morpheus.setProperty( "rank", "Captain" );	      
	         neo.createRelationshipTo( morpheus, RelTypes.KNOWS );
	         rel = morpheus.createRelationshipTo( trinity, RelTypes.KNOWS );
	         rel.setProperty( "age", "12 years" );
	         // create cypher node
	         Node cypher = this.graphDb.createNode();
	         cypher.setProperty( "last name", "Reagan" );
	         cypher.setProperty( "name", "Cypher" );	     
	         trinity.createRelationshipTo( cypher, RelTypes.KNOWS );
	         rel = morpheus.createRelationshipTo( cypher, RelTypes.KNOWS );
	         rel.setProperty( "disclosure", "public" );
	         // create smith node
	         Node smith = this.graphDb.createNode();
	         smith.setProperty( "language", "C++" );
	         smith.setProperty( "name", "Agent Smith" );
	         smith.setProperty( "version", "1.0b" );	        
	         rel = cypher.createRelationshipTo( smith, RelTypes.KNOWS );
	         rel.setProperty( "age", "6 months" );
	         rel.setProperty( "disclosure", "secret" );	        
	         // create architect node
	         Node architect = this.graphDb.createNode();
	         architect.setProperty( "name", "The Architect" );
	         smith.createRelationshipTo( architect, RelTypes.CODED_BY );
	 	     
	         tx.success();
	     }  finally {
	         tx.finish();
	     }
	 }


	public GraphDatabaseService getGraphDatabase() {
		return graphDb;
	}

}
