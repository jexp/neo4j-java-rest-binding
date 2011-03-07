package org.neo4j.rest.graphdb;

import org.apache.log4j.BasicConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.server.BootStrapper;

import java.net.URI;

public class RestTestBase {

    protected static GraphDatabaseService graphDb;
    private static BootStrapper bootStrapper;
    private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";
    private static final String CONFIG = RestTestBase.class.getResource("/neo4j-server.properties").getFile();

    @BeforeClass
    public static void startDb() throws Exception {
        BasicConfigurator.configure();
        System.setProperty("org.neo4j.server.properties", CONFIG);
        bootStrapper = new BootStrapper();
        bootStrapper.start();
        graphDb = new RestGraphDatabase(new URI(SERVER_ROOT_URI));
    }

    @AfterClass
    public static void shutdownDb() {
        //TODO fix bootStrapper.stop();
        bootStrapper.getServer().stop();

        graphDb.shutdown();
    }
}
