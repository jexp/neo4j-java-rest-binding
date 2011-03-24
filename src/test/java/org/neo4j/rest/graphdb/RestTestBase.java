package org.neo4j.rest.graphdb;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import org.apache.log4j.BasicConfigurator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.server.AddressResolver;
import org.neo4j.server.NeoServerWithEmbeddedWebServer;
import org.neo4j.server.modules.RESTApiModule;
import org.neo4j.server.modules.ThirdPartyJAXRSModule;
import org.neo4j.server.startup.healthcheck.StartupHealthCheck;
import org.neo4j.server.web.Jetty6WebServer;

import java.io.File;
import java.net.URI;

public class RestTestBase {

    protected static GraphDatabaseService graphDb;
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 7474;
    private static final String SERVER_ROOT_URI = "http://" + HOSTNAME + ":" + PORT + "/db/data/";
    private static final String SERVER_CLEANDB_URI = "http://" + HOSTNAME + ":" + PORT + "/cleandb/secret-key";
    private static final String CONFIG = RestTestBase.class.getResource("/neo4j-server.properties").getFile();
    private static NeoServerWithEmbeddedWebServer neoServer;

    @BeforeClass
    public static void startDb() throws Exception {
        BasicConfigurator.configure();
        neoServer = new NeoServerWithEmbeddedWebServer(new AddressResolver() {
            @Override
            public String getHostname() {
                return HOSTNAME;
            }
        }, new StartupHealthCheck(), new File(CONFIG), new Jetty6WebServer()) {
            protected void registerServerModules() {
                registerModule(RESTApiModule.class);
                registerModule(ThirdPartyJAXRSModule.class);
            }

            @Override
            protected int getWebServerPort() {
                return PORT;
            }
        };
        neoServer.start();

        graphDb = new RestGraphDatabase(new URI(SERVER_ROOT_URI));
    }

    @Before
    public void cleanDb() {
        ClientResponse response = Client
                .create().resource(SERVER_CLEANDB_URI)
                .delete(ClientResponse.class);

        if (response.getStatus() != 200) throw new RuntimeException("unable to clean database " + response);
    }

    @AfterClass
    public static void shutdownDb() {
        neoServer.stop();

        graphDb.shutdown();
    }
}
