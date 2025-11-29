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

    public List<Item> getItemsByUser(String userId) {
        return itemRepository.findByOwnerId(userId);
    }

    public List<Item> searchItems(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllItems();
        }
        return itemRepository.search(keyword);
    }

    public List<Item> searchItemsByUser(String userId, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getItemsByUser(userId);
        }
        return itemRepository.searchByOwner(userId, keyword);
    }

    public Item getItemById(String id) {
        return itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public void saveItem(Item item, MultipartFile[] files, Map<String, String> allParams, String userId,
            String username) {
        // Set owner information for new items
        if (item.getId() == null) {
            item.setOwnerId(userId);
            item.setOwnerUsername(username);
        }

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

    public boolean canUserAccessItem(String userId, String itemId, boolean isAdmin) {
        if (isAdmin) {
            return true;
        }
        Item item = getItemById(itemId);
        return item.getOwnerId() != null && item.getOwnerId().equals(userId);
    }
}
