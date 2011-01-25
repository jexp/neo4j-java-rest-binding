package org.neo4j.rest.graphdb;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author mh
 * @since 25.01.11
 */
public class GraphDatabaseFactory {
    public static GraphDatabaseService databaseFor(String url, String username, String password) {
        if (url.startsWith( "http://" ) || url.startsWith( "https://" )) {
            return new RestGraphDatabase( toURI( url ), username,password );
        }
        if (url.startsWith( "file:" )) {
            String path = toURI( url ).getPath();
            return new EmbeddedGraphDatabase( path );
        }
        return new EmbeddedGraphDatabase( url );
    }

    private static URI toURI( String uri ) {
        try {
            return new URI(uri);
        } catch ( URISyntaxException e ) {
            throw new RuntimeException( "Error using URI "+uri, e);
        }
    }
}
