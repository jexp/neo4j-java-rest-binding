package org.neo4j.rest.graphdb;

import org.neo4j.graphdb.*;
import org.neo4j.helpers.collection.MapUtil;



import java.net.URI;
import java.util.Map;

public class RestRelationship extends RestEntity implements Relationship {

    RestRelationship( URI uri, RestAPI restApi ) {
        super( uri, restApi );
    }

    RestRelationship( String uri, RestAPI restApi ) {
        super( uri, restApi );
    }

    public RestRelationship( Map<?, ?> data, RestAPI restApi ) {
        super( data, restApi );
    }

    public Node getEndNode() {
        return node( (String) getStructuralData().get( "end" ) );
    }

    public Node[] getNodes() {
        return new Node[]{
                node( (String) getStructuralData().get( "start" ) ),
                node( (String) getStructuralData().get( "end" ) )
        };
    }

    public Node getOtherNode( Node node ) {
        long nodeId = node.getId();
        String startNodeUri = (String) getStructuralData().get( "start" );
        String endNodeUri = (String) getStructuralData().get( "end" );
        if ( getEntityId( startNodeUri ) == nodeId ) {
            return node( endNodeUri );
        } else if ( getEntityId( endNodeUri ) == nodeId ) {
            return node( startNodeUri );
        } else {
            throw new NotFoundException( node + " isn't one of start/end for " + this );
        }
    }

    private RestNode node( String uri ) {
        return new RestNode( uri, getRestApi() );
    }

    public Node getStartNode() {
        return node( (String) getStructuralData().get( "start" ) );
    }

    public RelationshipType getType() {
        return DynamicRelationshipType.withName( (String) getStructuralData().get( "type" ) );
    }

    public boolean isType( RelationshipType type ) {
        return type.name().equals( getStructuralData().get( "type" ) );
    }
    
    
    public static Relationship create(RestNode startNode, RestNode endNode, RelationshipType type, Map<String, Object> props) {
        final RestRequest restRequest = startNode.getRestRequest();
        Map<String, Object> data = MapUtil.map("to", endNode.getUri(), "type", type.name());
        if (props!=null && props.size()>0) {
            data.put("data",props);
        }

        RequestResult requestResult = restRequest.post( "relationships", JsonHelper.createJsonFrom( data ) );
        if ( restRequest.statusOtherThan(requestResult, javax.ws.rs.core.Response.Status.CREATED ) ) {
            final int status = requestResult.getStatus();
            throw new RuntimeException( "" + status);
        }
        final URI location = requestResult.getLocation();
        return new RestRelationship(location, startNode.getRestApi() );
    }
   
}
