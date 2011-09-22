package org.neo4j.rest.graphdb;

/**
 * @author mh
 * @since 21.09.11
 */
public interface UpdatableRestResult<T> {
    void updateFrom(T newValue, RestAPI restApi);
}
