package com.example.jsondataset.repository;

import com.example.jsondataset.model.DatasetRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatasetRecordRepository extends JpaRepository<DatasetRecord, Long> {

    boolean existsByDatasetNameAndRecordKey(String datasetName, String recordKey);

    List<DatasetRecord> findByDatasetNameOrderByIdAsc(String datasetName);
}
