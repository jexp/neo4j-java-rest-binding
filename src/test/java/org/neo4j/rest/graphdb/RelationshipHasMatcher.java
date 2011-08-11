package org.neo4j.rest.graphdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class RelationshipHasMatcher extends TypeSafeMatcher<Iterable<Relationship>>{
	
	private final Node node;
	private final Direction direction;
	private final List<String> typeNames;

	public RelationshipHasMatcher(Node startNode, Direction direction, RelationshipType... types){
		this.node = startNode;
		this.direction = direction;		
		this.typeNames = fillTypeNames(Arrays.asList(types));
	}
	
	
	
	@Override
	public void describeTo(Description description) {
		description.appendText("Not all relationships matched the constraints. Node: ").appendValue(node).appendText(" direction: ").appendValue(direction).appendText(" relationship type(s): ").appendValue(typeNames);
		
	}

	@Override
	public boolean matchesSafely(Iterable<Relationship> relationships) {
		for (Relationship relationship : relationships) {
			
			boolean isStartnode = this.node.equals(relationship.getStartNode());
			boolean isEndnode = this.node.equals(relationship.getEndNode());
			if (!isStartnode && !isEndnode){
				return false;
			}
			
			Direction relationshipDirection = isStartnode ? Direction.OUTGOING : Direction.INCOMING;
			
			if (this.direction != null && this.direction!= relationshipDirection){
				return false;
			}
						
						
			if(!this.typeNames.isEmpty() && !this.typeNames.contains(relationship.getType().name())){				
				return false;
			}
		}
		
		return true;
	}
	
	public static RelationshipHasMatcher match(Node startNode, Direction direction, RelationshipType... types){
		return new RelationshipHasMatcher(startNode, direction, types);
	}
	
	public static List<String> fillTypeNames(List<RelationshipType> types){
		List<String> names = new ArrayList<String>();
		for (RelationshipType type : types) {
			names.add(type.name());
		}
		return names;
	}

}
