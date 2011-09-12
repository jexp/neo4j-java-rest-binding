package org.neo4j.rest.graphdb.util;


public interface ConvertedResult<R> extends Iterable<R> {
    R single();
    void handle(Handler<R> handler);
}
