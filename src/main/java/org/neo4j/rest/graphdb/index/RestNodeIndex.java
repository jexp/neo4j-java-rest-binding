package org.neo4j.rest.graphdb.index;

import org.neo4j.graphdb.Node;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.RestNode;
import org.neo4j.rest.graphdb.RestRequest;

import java.util.Map;

/**
 * @author mh
 * @since 24.01.11
 */
public class RestNodeIndex extends RestIndex<Node> {
    public RestNodeIndex( RestRequest restRequest, String indexName, RestAPI restApi ) {
        super( restRequest, indexName, restApi );
    }

    public Class<Node> getEntityType() {
        return Node.class;
    }

    protected Node createEntity(Map<?, ?> item) {
        return new RestNode((Map<?, ?>) item, restApi);
    }
}
