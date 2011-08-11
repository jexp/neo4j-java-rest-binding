package org.neo4j.rest.graphdb;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.PruneEvaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.helpers.Predicate;

/**
 * @author Michael Hunger
 * @since 03.02.11
 */
public interface RestTraversalDescription extends TraversalDescription {
	RestTraversalDescription prune(ScriptLanguage language, String code);
	
	RestTraversalDescription prune(PruneEvaluator pruneEvaluator);

	RestTraversalDescription filter(ScriptLanguage language, String code);

	RestTraversalDescription maxDepth(int depth);
	
	RestTraversalDescription breadthFirst();
	
	RestTraversalDescription relationships(RelationshipType relationshipType, Direction direction);
	
	RestTraversalDescription filter(Predicate<Path> pathPredicate);	
	

    public enum ScriptLanguage {
        JAVASCRIPT;
    }
}
