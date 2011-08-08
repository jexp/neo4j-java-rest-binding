package org.neo4j.rest.graphdb;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.rest.graphdb.MatrixDataGraph.RelTypes;
import org.neo4j.test.ImpermanentGraphDatabase;



/**
 * TestClass for the MatrixDatabase
 * @author Klemens Burchardi
 * @since 03.08.11
 */
public class MatrixDatabaseTest {
	private static GraphDatabaseService graphDb;
	private static MatrixDataGraph mdg;
	
	      @BeforeClass
	      public static void setUp() {
	    	  try {
				graphDb =  new ImpermanentGraphDatabase();
				mdg = new MatrixDataGraph(graphDb).createNodespace();				
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	      }
	  
	      @AfterClass
	      public static void tearDown() {
	          graphDb.shutdown();
	      }

	      
	      
	      
           
           @Test
           public void checkNeoProperties() throws Exception {
        	   Node neoNode = mdg.getNeoNode();    
        	   boolean isSetupCorrectly = false;
        	   if (neoNode.getProperty("age").equals(29)  &&
        		   neoNode.getProperty("name").equals("Thomas Anderson")){
        		   isSetupCorrectly = true;
        	   }        	   
        	   assertTrue(isSetupCorrectly);
           }
           
           /**
            * get the number of all nodes that know the neo node
            * @throws Exception
            */
           @Test
           public void getNeoFriends() throws Exception {
               Node neoNode = mdg.getNeoNode();             
               Traverser friendsTraverser = getFriends( neoNode );
               int numberOfFriends = 0;              
               for ( Path friendPath : friendsTraverser ) {               
                   numberOfFriends++;                 
               }
              
               assertEquals( 4, numberOfFriends );
           }
           
           /**
            * get the number of all heroes that are connected to the heroes collection node
            * @throws Exception
            */
           @Test
           public void checkNumberOfHeroes() throws Exception {                         
               Traverser heroesTraverser = getHeroes();
               int numberOfHeroes = 0;              
               for ( Path heroPath : heroesTraverser ) {               
            	   numberOfHeroes++;                 
               }
              
               assertEquals( 3, numberOfHeroes );
           }

           
           @Test
           public void checkForIndex() throws Exception {
        	   IndexManager index = graphDb.index();
        	   assertTrue(index.existsForNodes("heroes"));
           }
           
           @Test
           public void checkForHeroesCollection() throws Exception {
        	  Node heroesCollectionNode = mdg.getHeroesCollectionNode();
        	  assertEquals( "Heroes Collection", heroesCollectionNode.getProperty("type") );
           }
           
           @Test
           public void useMorpheusQuery() throws Exception {
        	   IndexManager index = graphDb.index();        	   
        	   Index<Node> goodGuys = index.forNodes("heroes");
        	   for (Node morpheus : goodGuys.query("name", "Morpheus")){
        		   assertEquals( "Morpheus", morpheus.getProperty("name") );
        	   }
           }
           
           @Test
           public void useTrinityIndex() throws Exception {
        	   IndexManager index = graphDb.index();        	   
        	   Index<Node> goodGuys = index.forNodes("heroes");
        	   IndexHits<Node> hits = goodGuys.get( "name", "Trinity" );
        	   Node trinity = hits.getSingle();
        	   assertEquals( "Trinity", trinity.getProperty("name") );
           }
           
          
           
           /**
            * returns a traverser for all nodes that have an outgoing relationship of the type KNOWS
            * @param person the startnode
            * @return the Traverser
            */
           private static Traverser getFriends( final Node person ) {
                    TraversalDescription td = Traversal.description()
                            .breadthFirst()                            
                            .relationships( RelTypes.KNOWS, Direction.OUTGOING )
                            .evaluator( Evaluators.excludeStartPosition() );
                    return td.traverse( person );
           }
           
           /**
            * returns a traverser for all nodes that have an outgoing relationship of the type HERO          
            * @return the Traverser
            */
           private static Traverser getHeroes() {
                    TraversalDescription td = Traversal.description()
                            .breadthFirst()                            
                            .relationships( RelTypes.HERO, Direction.OUTGOING )
                            .evaluator( Evaluators.excludeStartPosition() );
                    return td.traverse( mdg.getHeroesCollectionNode() );
           }

        
           
           
           /**
            * checks if neo has a friend named cypher
            * @throws Exception
            */
           @Test
           public void findCypher() throws Exception{
        	   Node neoNode = mdg.getNeoNode();              
               Traverser friendsTraverser = getFriends( neoNode );
               boolean foundCypher = false;
               for ( Path friendPath : friendsTraverser ) {            	  
            	   if (friendPath.endNode().getProperty("name").equals("Cypher")){
            		   foundCypher = true;
            		   break;
            	   }
               }
               assertTrue(foundCypher);
           } 
           
           /**
            * get all nodes that have an outgoing CODED_BY relationship
            * @throws Exception
            */
           @Test
           public void getMatrixHackers() throws Exception
           {
                    
               Traverser traverser = findHackers( mdg.getNeoNode() );
               int numberOfHackers = 0;
               for ( Path hackerPath : traverser ) {                  
                   numberOfHackers++;                  
               }              
               assertEquals( 1, numberOfHackers );
           }

          
           /**
            * returns a traverser for all nodes that have an outgoing CODED_BY relationship
            * based on a startnode
            * @param startNode the node to start from
            * @return the 'Traverser
            */
           private static Traverser findHackers( final Node startNode ) {
                    TraversalDescription td = Traversal.description()
                            .breadthFirst()
                            .relationships( RelTypes.CODED_BY, Direction.OUTGOING )
                            .relationships( RelTypes.KNOWS, Direction.OUTGOING )
                            .evaluator(
                                    Evaluators.returnWhereLastRelationshipTypeIs( RelTypes.CODED_BY ) );
                    return td.traverse( startNode );
                }

	      


}
