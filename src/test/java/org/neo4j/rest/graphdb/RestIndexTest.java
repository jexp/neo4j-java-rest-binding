package org.neo4j.rest.graphdb;

import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;

import java.util.*;

public class RestIndexTest extends RestTestBase {

    private static final String NODE_INDEX_NAME = "NODE_INDEX";
    private static final String REL_INDEX_NAME = "REL_INDEX";

    @Test
    public void testAddToNodeIndex() {
        nodeIndex().add(node(), "name", "test");
        IndexHits<Node> hits = nodeIndex().get("name", "test");
        Assert.assertEquals("index results", true, hits.hasNext());
        Assert.assertEquals(node(), hits.next());
    }

    @Test
    public void testNotFoundInNodeIndex() {
        IndexHits<Node> hits = nodeIndex().get("foo", "bar");
        Assert.assertEquals("no index results", false, hits.hasNext());
    }

    @Test
    public void testAddToRelationshipIndex() {
        final long value = System.currentTimeMillis();
        relationshipIndex().add(relationship(), "name", value);
        IndexHits<Relationship> hits = relationshipIndex().get("name", value);
        Assert.assertEquals("index results", true, hits.hasNext());
        Assert.assertEquals(relationship(), hits.next());
    }

    @Test
    public void testNotFoundInRelationshipIndex() {
        IndexHits<Relationship> hits = relationshipIndex().get("foo", "bar");
        Assert.assertEquals("no index results", false, hits.hasNext());
    }

    @Test
    public void testDeleteKeyValueFromNodeIndex() {
        String value = String.valueOf(System.currentTimeMillis());
        nodeIndex().add(node(), "time", value);
        IndexHits<Node> hits = nodeIndex().get("time", value);
        Assert.assertEquals("found in index results", true, hits.hasNext());
        Assert.assertEquals("found in index results", node(), hits.next());
        nodeIndex().remove(node(), "time", value);
        IndexHits<Node> hitsAfterRemove = nodeIndex().get("time", value);
        Assert.assertEquals("not found in index results", false, hitsAfterRemove.hasNext());
    }

    @Test
    public void testDeleteKeyFromNodeIndex() {
        String value = String.valueOf(System.currentTimeMillis());
        nodeIndex().add(node(), "time", value);
        IndexHits<Node> hits = nodeIndex().get("time", value);
        Assert.assertEquals("found in index results", true, hits.hasNext());
        Assert.assertEquals("found in index results", node(), hits.next());
        nodeIndex().remove(node(), "time");
        IndexHits<Node> hitsAfterRemove = nodeIndex().get("time", value);
        Assert.assertEquals("not found in index results", false, hitsAfterRemove.hasNext());
    }
    @Test
    public void testDeleteNodeFromNodeIndex() {
        String value = String.valueOf(System.currentTimeMillis());
        nodeIndex().add(node(), "time", value);
        IndexHits<Node> hits = nodeIndex().get("time", value);
        Assert.assertEquals("found in index results", true, hits.hasNext());
        Assert.assertEquals("found in index results", node(), hits.next());
        nodeIndex().remove(node());
        IndexHits<Node> hitsAfterRemove = nodeIndex().get("time", value);
        Assert.assertEquals("not found in index results", false, hitsAfterRemove.hasNext());
    }
    @Test
    public void testDeleteIndex() {
        final String indexName = nodeIndex().getName();
        nodeIndex().delete();
        final List<String> indexNames = Arrays.asList(getRestGraphDb().index().nodeIndexNames());
        Assert.assertEquals("removed index name",false,indexNames.contains(indexName));
    }
    @Test
    public void testCreateFulltextIndex() {
        Map<String,String> config=new HashMap<String, String>();
        config.put("provider", "lucene");
        config.put("type","fulltext");
        final IndexManager indexManager = getRestGraphDb().index();
        final Index<Node> index = indexManager.forNodes("fulltext", config);
        final Map<String, String> config2 = indexManager.getConfiguration(index);
        Assert.assertEquals("provider", config.get("provider"), config2.get("provider"));
        Assert.assertEquals("type", config.get("type"), config2.get("type"));
    }

    @Test
    public void testQueryFulltextIndexWithKey() {
        Map<String,String> config=new HashMap<String, String>();
        config.put("provider","lucene");
        config.put("type","fulltext");
        final Index<Node> index = getRestGraphDb().index().forNodes("text-index", config);
        index.add(node(),"text","any text");
        final IndexHits<Node> hits = index.query("text", "any t*");
        Assert.assertEquals("found in index results", true, hits.hasNext());
        Assert.assertEquals("found in index results", node(), hits.next());
    }
    @Test
    public void testQueryFulltextIndexWithOutKey() {
        Map<String,String> config=new HashMap<String, String>();
        config.put("provider","lucene");
        config.put("type","fulltext");
        final Index<Node> index = getRestGraphDb().index().forNodes("text-index", config);
        index.add(node(),"text","any text");
        final IndexHits<Node> hits = index.query("text:any t*");
        Assert.assertEquals("found in index results", true, hits.hasNext());
        Assert.assertEquals("found in index results", node(), hits.next());
    }

    @Test
    public void testDeleteFromRelationshipIndex() {
        String value = String.valueOf(System.currentTimeMillis());
        relationshipIndex().add(relationship(), "time", value);
        IndexHits<Relationship> hits = relationshipIndex().get("time", value);
        Assert.assertEquals("found in index results", true, hits.hasNext());
        Assert.assertEquals("found in index results", relationship(), hits.next());
        relationshipIndex().remove(relationship(), "time", value);
        IndexHits<Relationship> hitsAfterRemove = relationshipIndex().get("time", value);
        Assert.assertEquals("not found in index results", false, hitsAfterRemove.hasNext());
    }

    private Index<Node> nodeIndex() {
        return getRestGraphDb().index().forNodes(NODE_INDEX_NAME);
    }

    private RelationshipIndex relationshipIndex() {
        return getRestGraphDb().index().forRelationships(REL_INDEX_NAME);
    }

    @Test
    public void testNodeIndexIsListed() {
        nodeIndex().add(node(), "name", "test");
        Assert.assertTrue("node index name listed", Arrays.asList(getRestGraphDb().index().nodeIndexNames()).contains(NODE_INDEX_NAME));
    }

    @Test
    public void testRelationshipIndexIsListed() {
        relationshipIndex().add(relationship(), "name", "test");
        Assert.assertTrue("relationship index name listed", Arrays.asList(getRestGraphDb().index().relationshipIndexNames()).contains(REL_INDEX_NAME));
    }

}
