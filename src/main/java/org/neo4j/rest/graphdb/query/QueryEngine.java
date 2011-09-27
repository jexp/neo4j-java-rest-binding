package org.neo4j.rest.graphdb.query;

import java.util.Map;

import org.neo4j.rest.graphdb.util.QueryResult;

public interface QueryEngine<T> {
    QueryResult<T> query(String statement, Map<String, Object> params);

}
