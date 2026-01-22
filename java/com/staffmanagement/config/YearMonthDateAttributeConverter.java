package com.staffmanagement.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.YearMonth;
import java.sql.Date;
import java.time.LocalDate;

@Converter(autoApply = true)
public class YearMonthDateAttributeConverter implements AttributeConverter<YearMonth, Date> {

    @Override
    public Date convertToDatabaseColumn(YearMonth attribute) {
        if (attribute == null) {
            return null;
        }
        // Store as first day of month
        LocalDate localDate = attribute.atDay(1);
        return Date.valueOf(localDate);
    }

    @Override
    public YearMonth convertToEntityAttribute(Date dbData) {
        if (dbData == null) {
            return null;
        }
        LocalDate localDate = dbData.toLocalDate();
        return YearMonth.from(localDate);
    }
}