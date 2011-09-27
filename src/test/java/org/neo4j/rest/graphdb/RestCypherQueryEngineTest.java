package org.neo4j.rest.graphdb;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.rest.graphdb.MatrixDataGraph.RelTypes;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;


public class RestCypherQueryEngineTest extends RestTestBase {
    private RestCypherQueryEngine queryEngine;
    private RestAPI restAPI;
    private MatrixDataGraph embeddedMatrixdata;
    private MatrixDataGraph restMatrixData;
    
    @Before
    public void init() throws Exception {
        embeddedMatrixdata = new MatrixDataGraph(getGraphDatabase()).createNodespace();
        restMatrixData = new MatrixDataGraph(getRestGraphDb());
        this.restAPI = ((RestGraphDatabase)getRestGraphDb()).getRestAPI();
        queryEngine = new RestCypherQueryEngine(restAPI);      
    }
    
    @Test
    public void testGetReferenceNode(){
        final String queryString = "start n=(%reference) return n";
        final Node result = (Node) queryEngine.query(queryString, MapUtil.map("reference",0)).to(Node.class).single();
        assertEquals(embeddedMatrixdata.getGraphDatabase().getReferenceNode(), result);

    }
    
    @Test
    public void testGetNeoNode(){        
        final String queryString = "start neo=(%neo) return neo";
        final Node result = (Node) queryEngine.query(queryString, MapUtil.map("neo",getNeoId())).to(Node.class).single();
        assertEquals(embeddedMatrixdata.getNeoNode(), result);
    }
    
    @Test
    public void testGetNeoNodeByIndexLookup(){        
        final String queryString = "start neo=(heroes,name,\"%neoname\") return neo";
        final Node result = (Node) queryEngine.query(queryString, MapUtil.map("neoname","Neo")).to(Node.class).single();
        assertEquals(embeddedMatrixdata.getNeoNode(), result);
    }
    
    @Test
    public void testGetNeoNodeByIndexQuery(){        
        final String queryString = "start neo=(heroes,\"name:%neoname\") return neo";
        final Node result = (Node) queryEngine.query(queryString, MapUtil.map("neoname","Neo")).to(Node.class).single();
        assertEquals(embeddedMatrixdata.getNeoNode(), result);
    }
    
    @Test
    public void testGetNeoNodeSingleProperty(){       
        final String queryString = "start neo=(%neo) return neo.name";
        final String result = (String) queryEngine.query(queryString, MapUtil.map("neo",getNeoId())).to(String.class).single();
        assertEquals("Thomas Anderson", result);
    }
    
    @Test
    public void testGetNeoNodeViaMorpheus(){
        final String queryString = "start morpheus=(heroes,\"name:%morpheusname\") match (morpheus) <-[:KNOWS]- (neo) return neo";
        final Node result = (Node) queryEngine.query(queryString, MapUtil.map("morpheusname","Morpheus")).to(Node.class).single();
        assertEquals(embeddedMatrixdata.getNeoNode(), result);
    }
    
    @Test
    public void testGetCypherNodeViaMorpheusAndFilter(){
        final String queryString = "start morpheus=(heroes,\"name:%morpheusname\") match (morpheus) -[:KNOWS]-> (person) where person.type = \"villain\" return person";
        final Node result = (Node) queryEngine.query(queryString, MapUtil.map("morpheusname","Morpheus")).to(Node.class).single();
        assertEquals("Cypher", result.getProperty("name"));
    }
    
    @Test
    public void testGetArchitectViaMorpheusAndFilter(){
        final String queryString = "start morpheus=(heroes,\"name:%morpheusname\") match (morpheus) -[:KNOWS]-> (person) -[:KNOWS]-> (smith) -[:CODED_BY]-> (architect) where person.type = \"villain\" return architect";
        final Node result = (Node) queryEngine.query(queryString, MapUtil.map("morpheusname","Morpheus")).to(Node.class).single();
        assertEquals("The Architect", result.getProperty("name"));
    }
    
    
    @Test
    public void testGetNeoNodeMultipleProperties(){
        final String queryString = "start neo=(%neo) return neo.name, neo.type, neo.age";
        final Collection<Map<String,Object>> result = IteratorUtil.asCollection(queryEngine.query(queryString, MapUtil.map("neo",getNeoId())));
        assertEquals(asList( MapUtil.map("neo.name", "Thomas Anderson", "neo.type","hero", "neo.age", 29 )),result); 
        
    }
    
    @Test
    public void testGetRelationshipType(){
        final String queryString ="start n=(%reference) match (n)-[r]->() return r~TYPE";
        final Collection<String> result =  IteratorUtil.asCollection(queryEngine.query(queryString, MapUtil.map("reference",0)).to(String.class)); 
        assertTrue(result.contains("NEO_NODE"));      
    }
    
    
    public long getNeoId(){
        return  embeddedMatrixdata.getNeoNode().getId();
    }    
   
}
