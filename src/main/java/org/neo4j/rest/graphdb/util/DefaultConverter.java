package org.neo4j.rest.graphdb.util;

import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.rest.graphdb.traversal.NodePath;
import org.neo4j.rest.graphdb.traversal.RelationshipPath;


public class DefaultConverter<T,R> implements ResultConverter<T,R> {
    public R convert(Object value, Class type) {
        if (value == null || type.isInstance(value)) return (R) value;
        Object singleValue = extractValue(value);
        if (singleValue == null || type.isInstance(singleValue)) return (R) singleValue;
        final Class<?> sourceType = singleValue.getClass();
        Object result = doConvert(singleValue, sourceType, type);
        if (result == null)
            throw new RuntimeException("Cannot automatically convert " + sourceType + " to " + type + " please use a custom converter");
        return (R) result;
    }

    protected Object extractValue(Object value) {
        if (value instanceof Map) return extractSingle(((Map)value).values());
        if (value instanceof Iterable) return extractSingle((Iterable)value);
        return value;
    }

    private Object extractSingle(Iterable values) {
        final Iterator it = values.iterator();
        if (!it.hasNext()) throw new RuntimeException("Cannot extract single value from empty Iterable.");
        final Object result = it.next();
        if (it.hasNext()) throw new RuntimeException("Cannot extract single value from Iterable with more than one elements.");
        return result;
    }

    protected Object doConvert(Object value, Class<?> sourceType, Class type) {
        if (Node.class.isAssignableFrom(type)) {
            return toNode(value, sourceType);
        }
        if (Relationship.class.isAssignableFrom(type)) {
            return toRelationship(value, sourceType);
        }
        if (Path.class.isAssignableFrom(type)) {
            return toPath(value, sourceType);
        }
        if (type.isEnum()) {
            return Enum.valueOf(type, value.toString());
        }
        return null;
    }

    protected Path toPath(Object value, Class<?> sourceType) {
        if (Node.class.isAssignableFrom(sourceType)) return new NodePath((Node) value);
        if (Relationship.class.isAssignableFrom(sourceType)) return new RelationshipPath((Relationship) value);
        return null;
    }

    protected Relationship toRelationship(Object value, Class<?> sourceType) {
        if (Relationship.class.isAssignableFrom(sourceType)) return ((Relationship) value);
        if (Path.class.isAssignableFrom(sourceType)) return ((Path) value).lastRelationship();
        if (Node.class.isAssignableFrom(sourceType)) return ((Node) value).getRelationships().iterator().next();
        return null;
    }

    protected Node toNode(Object value, Class<?> sourceType) {
        if (Node.class.isAssignableFrom(sourceType)) return (Node)value;
        if (Path.class.isAssignableFrom(sourceType)) return ((Path) value).endNode();
        if (Relationship.class.isAssignableFrom(sourceType)) return ((Relationship) value).getEndNode();
        return null;
    }
}
