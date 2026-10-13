package com.example.jsondataset.controller;

import com.example.jsondataset.dto.InsertRecordResponse;
import com.example.jsondataset.service.DatasetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dataset/{datasetName}")
@Tag(name = "Dataset", description = "Insert JSON records and run group-by / sort-by queries")
public class DatasetController {

    private final DatasetService service;

    public DatasetController(DatasetService service) {
        this.service = service;
    }

    @Operation(summary = "Insert a JSON record into a dataset (the record must contain an 'id')")
    @PostMapping("/record")
    @ResponseStatus(HttpStatus.CREATED)
    public InsertRecordResponse insertRecord(@PathVariable String datasetName,
                                             @RequestBody Map<String, Object> record) {
        return service.insertRecord(datasetName, record);
    }

    @Operation(summary = "Group and/or sort the records of a dataset")
    @GetMapping("/query")
    public Map<String, Object> query(
            @PathVariable String datasetName,
            @Parameter(description = "Field to group by, e.g. department")
            @RequestParam(required = false) String groupBy,
            @Parameter(description = "Field to sort by, e.g. age")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "asc (default) or desc")
            @RequestParam(required = false) String order) {
        return service.query(datasetName, groupBy, sortBy, order);
    }
}
