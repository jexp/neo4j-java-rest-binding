package org.neo4j.rest.graphdb.util;

public interface ResultConverter<T, R> {
    R convert(T value, Class<R> type);

    ResultConverter NO_OP_RESULT_CONVERTER = new ResultConverter() {
        @Override
        public Object convert(Object value, Class type) {
            return null;
        }
    };

}
