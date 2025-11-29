package com.inventory.app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "categories")
public class Category {
    @Id
    private String id;
    private String name;
    private List<CategoryFieldTemplate> fieldTemplates = new ArrayList<>();
}
