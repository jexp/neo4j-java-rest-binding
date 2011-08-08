package org.neo4j.rest.graphdb;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
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
import org.neo4j.rest.graphdb.RestTraversalDescription.ScriptLanguage;

public class MatrixDatabaseRestTest extends RestTestBase{
	
	 private MatrixDataGraph embeddedmdg;
	 private MatrixDataGraph restmdg;
	
	 @Before
     public void matrixTestSetUp() {
		 	//fill server db with matrix nodes
		 this.embeddedmdg = new MatrixDataGraph(getGraphDatabase()).createNodespace();
		 this.restmdg = new MatrixDataGraph(getRestGraphDb());
     }
	 
	 @Test
	 public void testSetMaxtrixProperty() {
		 restmdg.getNeoNode().setProperty( "occupation", "the one" );
	     Node node = embeddedmdg.getNeoNode();	    
	     Assert.assertEquals( "the one", node.getProperty( "occupation" ) );
	 }
	 
	  @Test
      public void checkForIndex() throws Exception {
   	   IndexManager index = getRestGraphDb().index();
   	   assertTrue(index.existsForNodes("heroes"));
      }
	  
	  @Test
      public void useTrinityIndex() throws Exception {
   	   IndexManager index = getRestGraphDb().index();        	   
   	   Index<Node> goodGuys = index.forNodes("heroes");
   	   IndexHits<Node> hits = goodGuys.get( "name", "Trinity" );
   	   Node trinity = hits.getSingle();
   	   assertEquals( "Trinity", trinity.getProperty("name") );
      }
      
      @Test
      public void useMorpheusQuery() throws Exception {
   	   IndexManager index = getRestGraphDb().index();        	   
   	   Index<Node> goodGuys = index.forNodes("heroes");
   	   for (Node morpheus : goodGuys.query("name", "Morpheus")){
   		   assertEquals( "Morpheus", morpheus.getProperty("name") );
   	   }
      }
      
      /**
       * get the number of all nodes that know the neo node
       * @throws Exception
       */
      @Test
      public void getNeoFriends() throws Exception {
          Node neoNode = restmdg.getNeoNode();       
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
          Traverser heroesTraverser = getHeroesByRest();
          int numberOfHeroes = 0;              
          for ( Path heroPath : heroesTraverser ) {
        	  System.out.println(heroPath.endNode().getProperty("type"));
       	   numberOfHeroes++;                 
          }
         
          assertEquals( 3, numberOfHeroes );
      }
     
      /**
       * returns a traverser for all nodes that have an outgoing relationship of the type KNOWS
       * @param person the startnode
       * @return the Traverser
       */
      private static Traverser getFriends( final Node person ) {
               TraversalDescription td = RestTraversal.description()   
            		   .maxDepth(10)
                       .breadthFirst()                         
                       .relationships( RelTypes.KNOWS, Direction.OUTGOING )
                       .filter(Traversal.returnAllButStartNode());               
               return td.traverse( person );
           }
      
      /**
       * returns a traverser for all nodes that have an outgoing relationship of the type HERO          
       * @return the Traverser
       */
      private Traverser getHeroesByRest() {
    	 TraversalDescription td = RestTraversal.description()   
       		   .maxDepth(3)       	
               .breadthFirst()                        
               .relationships( RelTypes.PERSONS_REFERENCE, Direction.OUTGOING )
               .relationships( RelTypes.HEROES_REFERENCE, Direction.OUTGOING )
               .relationships( RelTypes.HERO, Direction.OUTGOING )
               .filter(Traversal.returnAllButStartNode())               
               .filter(ScriptLanguage.JAVASCRIPT, "position.length() == 3;"); 
    	  System.out.println(td.toString());
          return td.traverse(this.restmdg.getGraphDatabase().getReferenceNode());
      }

	
}
