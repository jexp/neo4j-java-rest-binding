package org.neo4j.rest.graphdb;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.rest.graphdb.MatrixDatabaseCreator.RelTypes;



/**
 * TestClass for the MatrixDatabase
 * @author Klemens Burchardi
 * @since 03.08.11
 */
public class MatrixDatabaseTest {
	private static GraphDatabaseService graphDb;
	
	      @BeforeClass
	      public static void setUp() {
	    	  graphDb = MatrixDatabaseCreator.getMatrixDatabase();
	      }
	  
	      @AfterClass
	      public static void tearDown() {
	          graphDb.shutdown();
	      }

	      
	      
	      /**
	      * Get the Neo node. (a.k.a. Thomas Anderson node)
	      *
	      * @return the Neo node
	      */
           private Node getNeoNode() {
               return graphDb.getReferenceNode().getSingleRelationship(
                       RelTypes.NEO_NODE, Direction.OUTGOING ).getEndNode();
           }
           
           @Test
           public void checkNeoProperties(){
        	   Node neoNode = getNeoNode();    
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
               Node neoNode = getNeoNode();             
               Traverser friendsTraverser = getFriends( neoNode );
               int numberOfFriends = 0;              
               for ( Path friendPath : friendsTraverser ) {               
                   numberOfFriends++;                 
               }
              
               assertEquals( 4, numberOfFriends );
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
            * checks if neo has a friend named cypher
            * @throws Exception
            */
           @Test
           public void findCypher() throws Exception{
        	   Node neoNode = getNeoNode();              
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
                    
               Traverser traverser = findHackers( getNeoNode() );
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
