package com.inventory.app.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryFieldTemplate {
    private String name;
    private FieldType fieldType;
    private boolean required;
    private List<String> dropdownOptions = new ArrayList<>();

    public enum FieldType {
        TEXT,
        NUMBER,
        DATE,
        DROPDOWN
    }
}
