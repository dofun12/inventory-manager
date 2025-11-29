package com.inventory.app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "items")
public class Item {
    @Id
    private String id;
    private String name;
    private Integer quantity;
    private String location;
    private String category;

    // Owner information
    private String ownerId;
    private String ownerUsername;

    // Paths to stored images
    private List<String> photos = new ArrayList<>();

    private String coverImage;

    // Dynamic attributes
    private Map<String, Object> attributes = new HashMap<>();
}
