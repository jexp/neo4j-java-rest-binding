package org.neo4j.rest.graphdb;

import java.util.List;
import java.util.Map;

public class RestResultException extends RuntimeException {
    public RestResultException(Object result) {
        super(format(toMap(result)));
    }

    private static String format(Map<?, ?> result) {
        if (result==null) return "Unknown Exception";
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

    public static boolean isExceptionResult(Object result) {
        final Map<String, Object> map = toMap(result);
        return map!=null && map.containsKey("exception") && map.containsKey("message");
    }

    private static Map<String, Object> toMap(Object result) {
        if (!(result instanceof Map)) return null;
        return (Map<String, Object>) result;

    }
}
