package org.neo4j.rest.graphdb.util;

public interface Handler<R> {
    void handle(R value);
}
