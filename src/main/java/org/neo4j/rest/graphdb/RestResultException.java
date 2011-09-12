package org.neo4j.rest.graphdb;

import java.util.List;
import java.util.Map;

public class RestResultException extends RuntimeException {
    public RestResultException(Map<?, ?> result) {
        super(format(result));
    }

    private static String format(Map<?, ?> result) {
        StringBuilder sb = new StringBuilder();
        sb.append(result.get("message")).append(" at\n");
        sb.append(result.get("exception")).append("\n");
        List<String> stacktrace = (List<String>) result.get("stacktrace");
        if (stacktrace != null) {
            for (String line : stacktrace) {
                sb.append("   ").append(line).append("\n");
            }
        }
        return sb.toString();
    }

    public static boolean isExceptionResult(Map<?, ?> result) {
        return result.containsKey("exception") && result.containsKey("message");
    }
}
