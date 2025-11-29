package com.inventory.app.repository;

import com.inventory.app.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ItemRepository extends MongoRepository<Item, String> {

    // Basic search by name or category
    List<Item> findByNameContainingIgnoreCase(String name);

    List<Item> findByCategoryContainingIgnoreCase(String category);

    // Advanced search could be done with Criteria API in Service,
    // but for simple cases this works.
    @Query("{ '$or': [ { 'name': { '$regex': ?0, '$options': 'i' } }, { 'category': { '$regex': ?0, '$options': 'i' } }, { 'location': { '$regex': ?0, '$options': 'i' } } ] }")
    List<Item> search(String keyword);
}
