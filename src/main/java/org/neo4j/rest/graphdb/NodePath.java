package org.neo4j.rest.graphdb;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;

public class NodePath implements Path {
    private final Node node;

    public NodePath(Node node) {
        this.node = node;
    }

    @Override
    public Node startNode() {
        return node;
    }

    @Override
    public Node endNode() {
        return node;
    }

    @Override
    public Relationship lastRelationship() {
        return null;
    }

    @Override
    public Iterable<Relationship> relationships() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<Node> nodes() {
        return asList(node);
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public Iterator<PropertyContainer> iterator() {
        return Arrays.<PropertyContainer>asList(node).iterator();
    }
}