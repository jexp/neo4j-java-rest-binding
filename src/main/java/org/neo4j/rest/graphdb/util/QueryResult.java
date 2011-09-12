package org.neo4j.rest.graphdb.util;


public interface QueryResult<T> extends Iterable<T> {
    <R> ConvertedResult<R> to(Class<R> type);
    <R> ConvertedResult<R> to(Class<R> type, ResultConverter<T, R> resultConverter);
    void handle(Handler<T> handler);
}
