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
import org.neo4j.helpers.Predicate;
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
          System.out.println(neoNode.getProperty("name"));
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
          Traverser heroesTraverser = getHeroesViaRest();
          int numberOfHeroes = 0;              
          for ( Path heroPath : heroesTraverser ) {        	
       	   numberOfHeroes++;                 
          }
         
          assertEquals( 3, numberOfHeroes );
      }
      
     
      /**
       * check if rest traversal returns the same as embedded traversal
       * @throws Exception
       */
      @Test
      public void checkTraverseByProperties() throws Exception {    	  
          Traverser heroesTraverserRest = getHeroesViaRest();
          Traverser heroesTraverserByProperties = getHeroesByNodeProperties();
          assertEquals( heroesTraverserRest.nodes().iterator().next().getId(), heroesTraverserByProperties.nodes().iterator().next().getId() );
      }
      
      /**
       * check if different REST Traversals for all heroes return the same 
       * @throws Exception
       */
      @Test
      public void checkTraverseByPropertiesRest() throws Exception {    	  
          Traverser heroesTraverserRest = getHeroesViaRest();
          Traverser heroesTraverserByPropertiesRest = getHeroesByNodePropertiesViaRest();
          assertEquals( heroesTraverserRest.nodes().iterator().next(), heroesTraverserByPropertiesRest.nodes().iterator().next() );
      }
      
      /**
       * check if rest traversal and traversal via the collection node return the same result
       * @throws Exception
       */
      @Test
      public void checkTraverseByCollectionNode() throws Exception {    	  
          Traverser heroesTraverserRest = getHeroesViaRest();
          Traverser heroesTraverserByCollection = getHeroesByCollectionNodeViaRest();
          assertEquals( heroesTraverserRest.nodes().iterator().next(), heroesTraverserByCollection.nodes().iterator().next() );
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
       * returns a traverser for all nodes that have an outgoing relationship of the type HERO an are 3 positions down in the path         
       * @return the Traverser
       */
      private Traverser getHeroesViaRest() {
    	 TraversalDescription td = RestTraversal.description()   
       		   .maxDepth(3)       	
               .breadthFirst()                        
               .relationships( RelTypes.PERSONS_REFERENCE, Direction.OUTGOING )
               .relationships( RelTypes.HEROES_REFERENCE, Direction.OUTGOING )
               .relationships( RelTypes.HERO, Direction.OUTGOING )                       
               .filter(ScriptLanguage.JAVASCRIPT, "position.length() == 3;"); 
          return td.traverse(this.restmdg.getGraphDatabase().getReferenceNode());
      }
      
      /**
       * returns a traverser for all nodes that have a hero relationship and are connected to the hero collection node
       * @return
       */
      private Traverser getHeroesByCollectionNodeViaRest(){
    	  TraversalDescription td = RestTraversal.description()   
       		   .maxDepth(10)
               .breadthFirst()                         
               .relationships( RelTypes.HERO, Direction.OUTGOING );                     
          return td.traverse( this.restmdg.getHeroesCollectionNode() );
      }
      
      /**
       * returns a traverser for all nodes that have a property type == hero via the REST API        
       * @return the Traverser
       */
      private Traverser getHeroesByNodePropertiesViaRest() {
    	 TraversalDescription td = RestTraversal.description()   
       		   .maxDepth(3)       	
               .breadthFirst()                        
               .relationships( RelTypes.PERSONS_REFERENCE, Direction.OUTGOING )
               .relationships( RelTypes.HEROES_REFERENCE, Direction.OUTGOING )
               .relationships( RelTypes.HERO, Direction.OUTGOING )                          
               .filter(ScriptLanguage.JAVASCRIPT, "position.endNode().getProperty('type','none') == 'hero';");    	
          return td.traverse(this.restmdg.getGraphDatabase().getReferenceNode());
      }
      
      
      /**
       * returns a traverser for all nodes that have a property type == hero in the embedded Database
       * @return the Traverser
       */
      private Traverser getHeroesByNodeProperties() {
    	  TraversalDescription td = Traversal.description()          		  	
                .breadthFirst()                        
                .relationships( RelTypes.PERSONS_REFERENCE, Direction.OUTGOING )
                .relationships( RelTypes.HEROES_REFERENCE, Direction.OUTGOING )
                .relationships( RelTypes.HERO, Direction.OUTGOING )                              
                .filter(new Predicate<Path>() { public boolean accept(Path path) { return path.endNode().getProperty("type","none").equals("hero");}});
    	 return td.traverse(this.embeddedmdg.getGraphDatabase().getReferenceNode());
      }

	
}
