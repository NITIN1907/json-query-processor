package com.example.jsondataset.service;

import com.example.jsondataset.dto.InsertRecordResponse;
import com.example.jsondataset.exception.ApiException;
import com.example.jsondataset.model.DatasetRecord;
import com.example.jsondataset.repository.DatasetRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class DatasetService {

    private static final Pattern DATASET_NAME = Pattern.compile("^[A-Za-z0-9_-]{1,100}$");

    private final DatasetRecordRepository repository;
    private final JsonPayloadConverter converter;
    private final RecordQueryProcessor processor;

    public DatasetService(DatasetRecordRepository repository,
                          JsonPayloadConverter converter,
                          RecordQueryProcessor processor) {
        this.repository = repository;
        this.converter = converter;
        this.processor = processor;
    }

    @Transactional
    public InsertRecordResponse insertRecord(String datasetName, Map<String, Object> record) {
        validateDatasetName(datasetName);
        if (record == null || record.isEmpty()) {
            throw ApiException.badRequest("Request body must be a non-empty JSON object");
        }
        Object id = record.get("id");
        if (id == null || String.valueOf(id).isBlank()) {
            throw ApiException.badRequest("Field 'id' is required");
        }
        String key = String.valueOf(id);
        if (repository.existsByDatasetNameAndRecordKey(datasetName, key)) {
            throw ApiException.conflict("Record with id '" + key + "' already exists in dataset '" + datasetName + "'");
        }
        repository.save(new DatasetRecord(datasetName, key, converter.toJson(record)));
        return new InsertRecordResponse("Record added successfully", datasetName, id);
    }

    /**
     * Runs group-by and/or sort-by on a dataset.
     * - groupBy only -> {"groupedRecords": {...}}
     * - sortBy only  -> {"sortedRecords": [...]}
     * - both         -> {"groupedRecords": {...}} with each group sorted
     */
    @Transactional(readOnly = true)
    public Map<String, Object> query(String datasetName, String groupBy, String sortBy, String order) {
        validateDatasetName(datasetName);
        boolean hasGroup = groupBy != null && !groupBy.isBlank();
        boolean hasSort = sortBy != null && !sortBy.isBlank();
        if (!hasGroup && !hasSort) {
            throw ApiException.badRequest("Provide at least one of 'groupBy' or 'sortBy'");
        }
        SortOrder sortOrder = SortOrder.from(order);

        List<Map<String, Object>> records = repository.findByDatasetNameOrderByIdAsc(datasetName).stream()
                .map(r -> converter.toMap(r.getPayload()))
                .toList();
        if (records.isEmpty()) {
            throw ApiException.notFound("Dataset '" + datasetName + "' not found or has no records");
        }

        Map<String, Object> response = new LinkedHashMap<>();
        if (hasGroup) {
            Map<String, List<Map<String, Object>>> grouped = processor.groupBy(records, groupBy);
            if (hasSort) {
                grouped.replaceAll((k, v) -> processor.sortBy(v, sortBy, sortOrder));
            }
            response.put("groupedRecords", grouped);
        } else {
            response.put("sortedRecords", processor.sortBy(records, sortBy, sortOrder));
        }
        return response;
    }

    private void validateDatasetName(String datasetName) {
        if (datasetName == null || !DATASET_NAME.matcher(datasetName).matches()) {
            throw ApiException.badRequest("Dataset name must be 1-100 characters: letters, digits, '_' or '-'");
        }
    }
}
