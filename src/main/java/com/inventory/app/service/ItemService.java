package com.inventory.app.service;

import com.inventory.app.model.Item;
import com.inventory.app.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final FileStorageService fileStorageService;

    public ItemService(ItemRepository itemRepository, FileStorageService fileStorageService) {
        this.itemRepository = itemRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> searchItems(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllItems();
        }
        return itemRepository.search(keyword);
    }

    public Item getItemById(String id) {
        return itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public void saveItem(Item item, MultipartFile[] files, Map<String, String> allParams) {
        // Handle dynamic attributes
        // Filter out known fields and keep only dynamic ones
        // This is a simplified approach. In a real controller, we might separate them
        // better.
        // For now, we'll assume the controller handles the map population or we do it
        // here if needed.
        // Actually, let's handle files here and let controller handle the map binding.

        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String filename = fileStorageService.store(file);
                    item.getPhotos().add(filename);
                }
            }
        }
        itemRepository.save(item);
    }

    public void deleteItem(String id) {
        itemRepository.deleteById(id);
    }
}
