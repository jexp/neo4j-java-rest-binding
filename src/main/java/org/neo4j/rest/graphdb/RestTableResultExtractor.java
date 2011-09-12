package org.neo4j.rest.graphdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestTableResultExtractor {

    private final RestEntityExtractor restEntityExtractor;

    public RestTableResultExtractor(RestEntityExtractor restEntityExtractor) {
        this.restEntityExtractor = restEntityExtractor;
    }

    public List<Map<String, Object>> extract(Map<?, ?> restResult) {
        List<String> columns = (List<String>) restResult.get("columns");
        return extractData(restResult, columns);
    }

    private List<Map<String, Object>> extractData(Map<?, ?> restResult, List<String> columns) {
        List<List<?>> rows = (List<List<?>>) restResult.get("data");
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(rows.size());
        for (List<?> row : rows) {
            result.add(mapRow(columns, row));
        }
        return result;
    }

    private Map<String, Object> mapRow(List<String> columns, List<?> row) {
        int columnCount = columns.size();
        Map<String, Object> newRow = new HashMap<String, Object>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            final Object value = row.get(i);
            newRow.put(columns.get(i), restEntityExtractor.convertFromRepresentation(value));
        }
        return newRow;
    }
}