package com.myapplication.LoanManagementSystem.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Frequency {
    WEEKLY,
    MONTHLY;

    /*@JsonCreator
    public static Frequency fromValue(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        if ("WEEKS".equals(normalized)) {
            return WEEKLY;
        }
        return Frequency.valueOf(normalized);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }*/
}




