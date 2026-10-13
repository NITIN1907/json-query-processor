package com.example.jsondataset.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure, stateless group-by / sort-by logic over JSON records (maps).
 * Kept separate from the service so it is trivial to unit test.
 */
@Component
public class RecordQueryProcessor {

    public Map<String, List<Map<String, Object>>> groupBy(List<Map<String, Object>> records, String field) {
        Map<String, List<Map<String, Object>>> groups = new LinkedHashMap<>();
        for (Map<String, Object> record : records) {
            Object value = record.get(field);
            String key = value == null ? "null" : String.valueOf(value);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
        }
        return groups;
    }

    /** Stable sort; records missing the field (null) always go last. */
    public List<Map<String, Object>> sortBy(List<Map<String, Object>> records, String field, SortOrder order) {
        Comparator<Object> valueComparator = RecordQueryProcessor::compareValues;
        if (order == SortOrder.DESC) {
            valueComparator = valueComparator.reversed();
        }
        Comparator<Map<String, Object>> comparator =
                Comparator.comparing((Map<String, Object> r) -> r.get(field), Comparator.nullsLast(valueComparator));
        return records.stream().sorted(comparator).toList();
    }

    private static int compareValues(Object a, Object b) {
        if (a instanceof Number x && b instanceof Number y) {
            return Double.compare(x.doubleValue(), y.doubleValue());
        }
        return String.valueOf(a).compareToIgnoreCase(String.valueOf(b));
    }
}

