package org.neo4j.rest.graphdb;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.net.URI;
import java.util.Iterator;

/**
 * @author Michael Hunger
 * @since 02.02.11
 */
public class RestTestBase {
    static int serverPort;
    static {
        serverPort = Integer.parseInt(System.getProperty("server.port","7474"));
    }
    static GraphDatabaseService graphDb;
    static final String SERVER_ROOT_URI = "http://localhost:"+ serverPort +"/db/data/";

    @BeforeClass
    public static void startDb() throws Exception {
        graphDb = new RestGraphDatabase( new URI( SERVER_ROOT_URI ) );
    }

    @AfterClass
    public static void shutdownDb() {
        graphDb.shutdown();
    }

    protected Relationship relationship() {
        Iterator<Relationship> it = node().getRelationships( Type.TEST, Direction.OUTGOING ).iterator();
        if ( it.hasNext() ) return it.next();
        return node().createRelationshipTo( graphDb.createNode(), Type.TEST );
    }

    protected Node node() {
        return graphDb.getReferenceNode();
    }
}
