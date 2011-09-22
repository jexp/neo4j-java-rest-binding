package org.neo4j.rest.graphdb;

import org.neo4j.helpers.collection.IterableWrapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.codehaus.groovy.runtime.InvokerHelper.asList;

/**
 * @author mh
 * @since 21.09.11
 */
public class RestCollectionConverter implements RestResultConverter {

    RestResultConverters converters;

    public RestCollectionConverter(RestResultConverter...converters) {
        this.converters = new RestResultConverters(converters);
    }

    @Override
    public boolean canHandle(Object value) {
        if (!(value instanceof Iterable)) return false;
        final Iterator it = ((Iterable) value).iterator();
        return !it.hasNext() || converters.canHandle(it.next());
    }

    @Override
    public Object convertFromRepresentation(Object values) {
        Iterable it= (Iterable) values;
        return new IterableWrapper<Object,Object>(it) {
            @Override
            protected Object underlyingObjectToObject(Object object) {
                return converters.convertFromRepresentation(object);
            }
        };
    }
}
