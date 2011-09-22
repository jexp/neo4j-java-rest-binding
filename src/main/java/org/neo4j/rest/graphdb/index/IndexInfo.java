package org.neo4j.rest.graphdb.index;

import java.util.Map;

/**
 * @author mh
 * @since 22.09.11
 */
public interface IndexInfo {
    boolean checkConfig(String indexName, Map<String, String> config);

    String[] indexNames();

    boolean exists(String indexName);

    Map<String, String> getConfig(String name);
}
