package com.example.jsondataset.service;

import com.example.jsondataset.exception.ApiException;

public enum SortOrder {
    ASC, DESC;

    /** Parses "asc"/"desc" (case-insensitive). Null or blank defaults to ASC. */
    public static SortOrder from(String value) {
        if (value == null || value.isBlank()) {
            return ASC;
        }
        return switch (value.trim().toLowerCase()) {
            case "asc" -> ASC;
            case "desc" -> DESC;
            default -> throw ApiException.badRequest("Invalid 'order' value '" + value + "'. Use 'asc' or 'desc'");
        };
    }
}