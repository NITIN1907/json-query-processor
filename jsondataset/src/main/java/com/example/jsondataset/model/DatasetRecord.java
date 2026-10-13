package com.example.jsondataset.model;

import jakarta.persistence.*;

/**
 * One JSON record belonging to a named dataset.
 * The JSON itself is stored as text in {@code payload}; {@code recordKey} is the
 * record's own "id" value and is unique within a dataset.
 */
@Entity
@Table(name = "dataset_records",
        uniqueConstraints = @UniqueConstraint(columnNames = {"dataset_name", "record_key"}),
        indexes = @Index(name = "idx_dataset_name", columnList = "dataset_name"))
public class DatasetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dataset_name", nullable = false)
    private String datasetName;

    @Column(name = "record_key", nullable = false)
    private String recordKey;

    @Lob
    @Column(nullable = false)
    private String payload;

    protected DatasetRecord() {
        // required by JPA
    }

    public DatasetRecord(String datasetName, String recordKey, String payload) {
        this.datasetName = datasetName;
        this.recordKey = recordKey;
        this.payload = payload;
    }

    public Long getId() { return id; }
    public String getDatasetName() { return datasetName; }
    public String getRecordKey() { return recordKey; }
    public String getPayload() { return payload; }
}
