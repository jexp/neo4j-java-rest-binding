package org.neo4j.rest.graphdb;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Michael Hunger
 * @since 02.02.11
 */
public class RestTraversal implements RestTraversalDescription {

    private static final String FULLPATH = "fullpath";
    private final Map<String, Object> description=new HashMap<String, Object>();

    @Override
    public String toString() {
        return description.toString();
    }

    public RestTraversalDescription uniqueness(UniquenessFactory uniquenessFactory) {
        return uniqueness(uniquenessFactory,null);
    }

    public RestTraversalDescription uniqueness(UniquenessFactory uniquenessFactory, Object value) {
        String uniqueness = restify(uniquenessFactory);
        add("uniqueness",value==null ? uniqueness : toMap("name",uniqueness, "value", value));
        return null;
    }

    private String restify(UniquenessFactory uniquenessFactory) {
        if (uniquenessFactory instanceof Uniqueness) {
            return ((Uniqueness)uniquenessFactory).name().toLowerCase().replace("_"," ");
        }
        throw new UnsupportedOperationException("Only values of "+Uniqueness.class+" are supported");
    }

    public RestTraversalDescription prune(PruneEvaluator pruneEvaluator) {
    	if (pruneEvaluator == PruneEvaluator.NONE) {
              return add( "prune_evaluator", toMap( "language", "builtin", "name", "none" ) );
        }
        Integer maxDepth= getMaxDepthValueOrNull(pruneEvaluator);
        if (maxDepth!=null) {
            return maxDepth(maxDepth);
        }
        throw new UnsupportedOperationException("Only max depth supported");
    }

    private Integer getMaxDepthValueOrNull(PruneEvaluator pruneEvaluator) {
        try {
            final Field depthField = pruneEvaluator.getClass().getDeclaredField("val$depth");
            depthField.setAccessible(true);
            return (Integer) depthField.get(pruneEvaluator);
        } catch (Exception e) {
            return null;
        }
    }

    public RestTraversalDescription filter(Predicate<Path> pathPredicate) {
        if (pathPredicate == Traversal.returnAll()) return add("return_filter",toMap("language","builtin", "name","all"));
        if (pathPredicate == Traversal.returnAllButStartNode()) return add("return_filter",toMap("language","builtin", "name","all_but_start_node"));
        throw new UnsupportedOperationException("Only builtin paths supported");
    }

    public RestTraversalDescription evaluator(Evaluator evaluator) {
    	 throw new UnsupportedOperationException();
    }

    public RestTraversalDescription prune(ScriptLanguage language, String code) {
        return add("prune_evaluator",toMap("language",language.name().toLowerCase(),"body",code ));
    }

    public RestTraversalDescription filter(ScriptLanguage language, String code) {
        return add("return_filter",toMap("language",language.name().toLowerCase(),"body",code ));
    }

    public RestTraversalDescription maxDepth(int depth) {
        return add("max_depth",depth);
    }

    public RestTraversalDescription order(BranchOrderingPolicy branchOrderingPolicy) {
        throw new UnsupportedOperationException();
    }

    public RestTraversalDescription depthFirst() {
        return add("order","depth_first");
    }

    public RestTraversalDescription breadthFirst() {
        return add("order", "breadth_first");
    }

    private RestTraversalDescription add(String key, Object value) {
        description.put(key,value);
        return this;
    }

    public RestTraversalDescription relationships(RelationshipType relationshipType) {
        return relationships(relationshipType, null);
    }

    public RestTraversalDescription relationships(RelationshipType relationshipType, Direction direction) {
        if (!description.containsKey("relationships")) {
            description.put("relationships",new HashSet<Map<String,Object>>());
        }
        Set<Map<String,Object>> relationships= (Set<Map<String, Object>>) description.get("relationships");
        relationships.add(toMap("type", relationshipType, "direction", directionString(direction)));
        return this;
    }

    private Map<String, Object> toMap(Object...params) {
        if (params.length % 2 != 0) throw new IllegalArgumentException("toMap needs an even number of arguments, but was "+Arrays.toString(params));

        Map<String, Object> result = new HashMap<String, Object>();
        for (int i = 0; i < params.length; i+=2) {
            if (params[i+1] == null) continue;
            result.put(params[i].toString(), params[i + 1].toString());
        }
        return result;
    }

    private String directionString(Direction direction) {
        return RestDirection.from(direction).shortName;
    }

    public RestTraversalDescription expand(RelationshipExpander relationshipExpander) {
        return null;
    }

    public Traverser traverse(Node node) {
        final RestNode restNode = (RestNode) node;
        final RestRequest request = restNode.getRestRequest();
        final String traversalJson = JsonHelper.createJsonFrom(description);
        final RequestResult result = request.post("traverse/" + FULLPATH, traversalJson);
        if (request.statusOtherThan(result, Response.Status.OK)) throw new RuntimeException(String.format("Error executing traversal: %d %s",result.getStatus(), traversalJson));
        final Object col = request.toEntity(result);
        if (!(col instanceof Collection)) throw new RuntimeException(String.format("Unexpected traversal result, %s instead of collection", col!=null ? col.getClass() : null));
        return new RestTraverser((Collection) col,restNode.getRestApi());
    }

    public static RestTraversalDescription description() {
        return new RestTraversal();
    }

    public Map<String,Object> getPostData() {
        return description;
    }
}
