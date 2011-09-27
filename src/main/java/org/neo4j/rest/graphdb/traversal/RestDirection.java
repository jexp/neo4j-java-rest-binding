package org.neo4j.rest.graphdb.traversal;

import org.neo4j.graphdb.Direction;

public enum RestDirection {
    INCOMING( Direction.INCOMING, "incoming", "in" ),
    OUTGOING( Direction.OUTGOING, "outgoing", "out" ),
    BOTH( Direction.BOTH, "all", "all" );

    public final Direction direction;
    public final String longName;
    public final String shortName;

    RestDirection( Direction direction, String longName, String shortName ) {
        this.direction = direction;
        this.longName = longName;
        this.shortName = shortName;
    }

    public static RestDirection from( Direction direction ) {
        for ( RestDirection restDirection : values() ) {
            if ( restDirection.direction == direction ) return restDirection;
        }
        throw new RuntimeException( "No Rest-Direction for " + direction );
    }
}