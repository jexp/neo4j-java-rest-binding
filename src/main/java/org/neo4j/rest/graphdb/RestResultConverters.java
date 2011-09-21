package org.neo4j.rest.graphdb;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author mh
 * @since 21.09.11
 */
class RestResultConverters implements RestResultConverter {
    private final Collection<RestResultConverter> converters;

    public RestResultConverters(RestResultConverter... converters) {
        this.converters = Arrays.asList(converters);
    }

    @Override
    public boolean canHandle(Object value) {
        for (RestResultConverter converter : converters) {
            if (converter.canHandle(value)) return true;
        }
        return false;
    }

    @Override
    public Object convertFromRepresentation(Object value) {
        for (RestResultConverter converter : converters) {
            if (converter.canHandle(value)) {
                return converter.convertFromRepresentation(value);
            }
        }
        return null;
    }
}
