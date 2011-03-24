package org.neo4j.rest.graphdb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Uniqueness;

import java.util.Map;

/**
 * @author Michael Hunger
 * @since 03.02.11
 */
public class RestTraversalTest {
    private RestTraversal traversalDescription;

    @Before
    public void setUp() throws Exception {
        traversalDescription = (RestTraversal) RestTraversal.description();
    }

    @Test
    public void testUniqueness() throws Exception {
        traversalDescription.uniqueness(Uniqueness.NODE_PATH);
        Assert.assertEquals("node path", traversalDescription.getPostData().get("uniqueness"));
    }

    @Test
    public void testUniquenessWithValue() throws Exception {
        traversalDescription.uniqueness(Uniqueness.NODE_PATH,"test");
        final Map uniquenessMap = (Map) traversalDescription.getPostData().get("uniqueness");
        Assert.assertEquals("node path", uniquenessMap.get("name"));
        Assert.assertEquals("test", uniquenessMap.get("value"));
    }

    @Test
    public void testPruneScript() throws Exception {
        traversalDescription.prune(RestTraversalDescription.ScriptLanguage.JAVASCRIPT, "return true;");

    }

    @Test
    public void testFilterScript() throws Exception {

    }

    @Test
    public void testEvaluator() throws Exception {

    }

    @Test
    public void testPrune() throws Exception {

    }

    @Test
    public void testFilter() throws Exception {

    }

    @Test
    public void testMaxDepth() throws Exception {

    }

    @Test
    public void testOrder() throws Exception {

    }

    @Test
    public void testDepthFirst() throws Exception {

    }

    @Test
    public void testBreadthFirst() throws Exception {

    }

    @Test
    public void testRelationships() throws Exception {

    }

    @Test
    public void testRelationshipsAndDirection() throws Exception {

    }

    @Test
    public void testExpand() throws Exception {

    }
    @Test
    public void testComplexTraversal() throws Exception {

    }
}
