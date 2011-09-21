package org.neo4j.rest.graphdb;

import static java.util.Arrays.asList;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.helpers.collection.CombiningIterable;
import org.neo4j.helpers.collection.IterableWrapper;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.rest.graphdb.RequestResult;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

public class RestNode extends RestEntity implements Node {
    public RestNode( URI uri, RestAPI restApi ) {
        super( uri, restApi );
    }

    public RestNode( String uri, RestAPI restApi ) {
        super( uri, restApi );
    }

    public RestNode( Map<?, ?> data, RestAPI restApi ) {
        super( data, restApi );
    }    
  
    public Relationship createRelationshipTo( Node toNode, RelationshipType type ) {
    	 return this.restApi.createRelationship(this,(RestNode)toNode,type,null);
    }

    public Iterable<Relationship> getRelationships() {
        return restApi.wrapRelationships( restRequest.get( "relationships/all" ) );
    }

    public Iterable<Relationship> getRelationships( RelationshipType... types ) {
        String path = getStructuralData().get( "all_relationships" ) + "/";
        int counter = 0;
        for ( RelationshipType type : types ) {
            if ( counter++ > 0 ) {
                path += "&";
            }
            path += type.name();
        }
        return restApi.wrapRelationships( restRequest.get( path ) );
    }


    public Iterable<Relationship> getRelationships( Direction direction ) {
        return restApi.wrapRelationships( restRequest.get( "relationships/" + RestDirection.from( direction ).shortName ) );
    }

    public Iterable<Relationship> getRelationships( RelationshipType type,
                                                    Direction direction ) {
        String relationshipsKey = RestDirection.from( direction ).longName + "_relationships";
        Object relationship = getStructuralData().get( relationshipsKey );
        return restApi.wrapRelationships( restRequest.get( relationship + "/" + type.name() ) );
    }

    public Relationship getSingleRelationship( RelationshipType type,
                                               Direction direction ) {
        return IteratorUtil.singleOrNull( getRelationships( type, direction ) );
    }

    public boolean hasRelationship() {
        return getRelationships().iterator().hasNext();
    }

    public boolean hasRelationship( RelationshipType... types ) {
        return getRelationships( types ).iterator().hasNext();
    }

    public boolean hasRelationship( Direction direction ) {
        return getRelationships( direction ).iterator().hasNext();
    }

    public boolean hasRelationship( RelationshipType type, Direction direction ) {
        return getRelationships( type, direction ).iterator().hasNext();
    }

    public Traverser traverse( Order order, StopEvaluator stopEvaluator,
                               ReturnableEvaluator returnableEvaluator, Object... rels ) {
        throw new UnsupportedOperationException();
    }

    public Traverser traverse( Order order, StopEvaluator stopEvaluator,
                               ReturnableEvaluator returnableEvaluator, RelationshipType type, Direction direction ) {
        throw new UnsupportedOperationException();
    }

    public Traverser traverse( Order order, StopEvaluator stopEvaluator,
                               ReturnableEvaluator returnableEvaluator, RelationshipType type, Direction direction,
                               RelationshipType secondType, Direction secondDirection ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<Relationship> getRelationships(final Direction direction, RelationshipType... types) {
        return new CombiningIterable<Relationship>(new IterableWrapper<Iterable<Relationship>, RelationshipType>(asList(types)) {
            @Override
            protected Iterable<Relationship> underlyingObjectToObject(RelationshipType relationshipType) {
                return getRelationships(relationshipType,direction);
            }
        });
    }

    @Override
    public boolean hasRelationship(Direction direction, RelationshipType... types) {
        for (RelationshipType relationshipType : types) {
            if (hasRelationship(relationshipType,direction)) return true;
        }
        return false;
    }
}
