package com.myapplication.LoanManagementSystem.utils;


import com.myapplication.LoanManagementSystem.model.Frequency;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FrequencyConverter implements AttributeConverter<Frequency, String> {

    @Override
    public String convertToDatabaseColumn(Frequency attribute) {
        if (attribute == null) {
            return null;
        }
        // Store the enum value as you want in the database.
        // For instance, if you want to always store "WEEKLY", even though the enum constant is WEEKLY.
        // You can just use attribute.name() here.
        return attribute.name();
    }

    @Override
    public Frequency convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        String normalized = dbData.trim().toUpperCase();
        // Map "WEEKS" from the DB to the WEEKLY enum constant.
        if ("WEEKS".equals(normalized)) {
            return Frequency.WEEKLY;
        }
        // Otherwise, try to convert directly.
        return Frequency.valueOf(normalized);
    }
}

