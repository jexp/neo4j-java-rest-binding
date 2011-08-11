package org.neo4j.rest.graphdb;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;


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
	     FIGHTS,
	     CODED_BY,
	     PERSONS_REFERENCE,	    
	     HEROES_REFERENCE,
	     HERO,
	     VILLAINS_REFERENCE,
	     VILLAIN
	 }	 
	
	 private GraphDatabaseService graphDb;
	 
	 public MatrixDataGraph(GraphDatabaseService graphDb){
		 this.graphDb = graphDb;		
	 }
		
	 
	 /**
	  * fills the database with nodes and relationships, using the matrix example
	  * @param graphDb the graph database to fill 
	  * @return MatrixDataGraph the instance for chaining purposes
	  */
	 public MatrixDataGraph createNodespace() {
	      Transaction tx = this.graphDb.beginTx();
	      try {
	    	 //create the index for all characters that are considered good guys (sorry cypher) 
	    	 IndexManager index = this.graphDb.index();
	    	 Index<Node> goodGuys = index.forNodes("heroes");
	    	//create persons collection node
	    	 Node persons = this.graphDb.createNode();
	    	 persons.setProperty("type", "Persons Collection");	    	 
	    	 //create heroes collection node
	    	 Node heroes = this.graphDb.createNode();
	    	 heroes.setProperty("type", "Heroes Collection");
	    	//create villains collection node
	    	 Node villains = this.graphDb.createNode();
	    	 villains.setProperty("type", "Villains Collection");
	    	 // create neo node
	         Node neo = this.graphDb.createNode();
	         neo.setProperty( "age", 29 );
	         neo.setProperty( "name", "Thomas Anderson" );
	         neo.setProperty("type", "hero");
	         goodGuys.add(neo, "name", "Neo");	         
	         Node referenceNode = this.graphDb.getReferenceNode();	        
	         // connect the persons collection node to the reference node
	         referenceNode.createRelationshipTo( persons, RelTypes.PERSONS_REFERENCE);
	         // connect the heroes collection node to the persons collection node
	         persons.createRelationshipTo( heroes, RelTypes.HEROES_REFERENCE);
	         // connect the villains collection node to the persons collection node
	         persons.createRelationshipTo( villains, RelTypes.VILLAINS_REFERENCE);
	         // connect neo to the reference node
	         referenceNode.createRelationshipTo( neo, RelTypes.NEO_NODE );
	         // connect neo to the heroes collection node
	         heroes.createRelationshipTo( neo, RelTypes.HERO);
	         // create trinity node
	         Node trinity = this.graphDb.createNode();
	         trinity.setProperty( "name", "Trinity" );
	         trinity.setProperty("type", "hero");
	         goodGuys.add(trinity, "name", "Trinity");
	         Relationship rel = neo.createRelationshipTo( trinity, RelTypes.KNOWS );
	         rel.setProperty( "age", "3 days" );
	         // connect trinity to the heroes collection node
	         heroes.createRelationshipTo( trinity, RelTypes.HERO);
	         // create morpheus node
	         Node morpheus = this.graphDb.createNode();
	         morpheus.setProperty( "name", "Morpheus" );
	         morpheus.setProperty( "occupation", "Total badass" );
	         morpheus.setProperty( "rank", "Captain" );	
	         morpheus.setProperty("type", "hero");
	         goodGuys.add(morpheus, "name", "Morpheus");
	         neo.createRelationshipTo( morpheus, RelTypes.KNOWS );
	         rel = morpheus.createRelationshipTo( trinity, RelTypes.KNOWS );
	         rel.setProperty( "age", "12 years" );
	         // connect morpheus to the heroes collection node
	         heroes.createRelationshipTo( morpheus, RelTypes.HERO);
	         // create cypher node
	         Node cypher = this.graphDb.createNode();
	         cypher.setProperty( "last name", "Reagan" );
	         cypher.setProperty( "name", "Cypher" );	
	         cypher.setProperty("type", "villain");
	         trinity.createRelationshipTo( cypher, RelTypes.KNOWS );
	         rel = morpheus.createRelationshipTo( cypher, RelTypes.KNOWS );
	         rel.setProperty( "disclosure", "public" );
	         // connect cypher to the villains collection node
	         villains.createRelationshipTo( cypher, RelTypes.VILLAIN);
	         // create smith node
	         Node smith = this.graphDb.createNode();
	         smith.setProperty( "language", "C++" );
	         smith.setProperty( "name", "Agent Smith" );
	         smith.setProperty( "version", "1.0b" );
	         smith.setProperty("type", "villain");
	         neo.createRelationshipTo( smith, RelTypes.FIGHTS );
	         rel = cypher.createRelationshipTo( smith, RelTypes.KNOWS );
	         rel.setProperty( "age", "6 months" );
	         rel.setProperty( "disclosure", "secret" );	 
	         // connect smith to the villains collection node
	         villains.createRelationshipTo( smith, RelTypes.VILLAIN);
	         // create architect node
	         Node architect = this.graphDb.createNode();
	         architect.setProperty( "name", "The Architect" );
	         smith.createRelationshipTo( architect, RelTypes.CODED_BY );
	 	     
	         tx.success();
	     }  finally {
	         tx.finish();
	        
	     }
	      return this;
	 }


	public GraphDatabaseService getGraphDatabase() {
		return graphDb;
	}
	
	 /**
	  * Get the Neo node. (a.k.a. Thomas Anderson node)
	  *
	  * @return the Neo node
	  */
	 public Node getNeoNode() {
	      return this.graphDb.getReferenceNode().getSingleRelationship(
	              RelTypes.NEO_NODE, Direction.OUTGOING ).getEndNode();
	 }
      
     /**
      * Get the Persons Collection node
      *
      * @return the Persons Collection node
      */
       public Node getPersonsCollectionNode() {
           return this.graphDb.getReferenceNode().getSingleRelationship(
                   RelTypes.PERSONS_REFERENCE, Direction.OUTGOING ).getEndNode();
       }
        
      /**
       * Get the Heroes Collection node
       *
       * @return the Heroes Collection node
       */
      public Node getHeroesCollectionNode() {
            return this.getPersonsCollectionNode().getSingleRelationship(
                   RelTypes.HEROES_REFERENCE, Direction.OUTGOING ).getEndNode();
      }
      
      /**
       * Get the Villains Collection node
       *
       * @return the Villains Collection node
       */
      public Node getVillainsCollectionNode() {
            return this.getPersonsCollectionNode().getSingleRelationship(
                   RelTypes.VILLAINS_REFERENCE, Direction.OUTGOING ).getEndNode();
      }

}
