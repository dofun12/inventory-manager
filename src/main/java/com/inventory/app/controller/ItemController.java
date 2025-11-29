package com.inventory.app.controller;

import com.inventory.app.model.Item;
import com.inventory.app.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final com.inventory.app.repository.CategoryRepository categoryRepository;

    public ItemController(ItemService itemService, com.inventory.app.repository.CategoryRepository categoryRepository) {
        this.itemService = itemService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String listItems(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("items", itemService.searchItems(keyword));
        model.addAttribute("keyword", keyword);
        return "items/list";
    }

    @GetMapping("/new")
    public String newItemForm(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("categories", categoryRepository.findAll());
        return "items/form";
    }

    @PostMapping
    public String saveItem(@ModelAttribute Item item,
            @RequestParam("images") MultipartFile[] files,
            @RequestParam(value = "deletedImages", required = false) List<String> deletedImages,
            @RequestParam Map<String, String> allParams) {

        System.out.println("DEBUG: saveItem called");
        System.out.println("DEBUG: Item ID: " + item.getId());
        System.out.println("DEBUG: Cover Image from Form: " + item.getCoverImage());
        System.out.println("DEBUG: Deleted Images: " + deletedImages);

        Item itemToSave;
        if (item.getId() != null && !item.getId().isEmpty()) {
            // Edit mode: fetch existing to preserve photos
            itemToSave = itemService.getItemById(item.getId());
            System.out.println("DEBUG: Existing Item Photos: " + itemToSave.getPhotos());
            System.out.println("DEBUG: Existing Item Cover: " + itemToSave.getCoverImage());

            itemToSave.setName(item.getName());
            itemToSave.setCategory(item.getCategory());
            itemToSave.setQuantity(item.getQuantity());
            itemToSave.setLocation(item.getLocation());

            // Handle deletions
            if (deletedImages != null && !deletedImages.isEmpty()) {
                System.out.println("DEBUG: Removing images: " + deletedImages);
                itemToSave.getPhotos().removeAll(deletedImages);
                // If cover image was deleted, reset it
                if (deletedImages.contains(itemToSave.getCoverImage())) {
                    System.out.println("DEBUG: Cover image was deleted, resetting to null");
                    itemToSave.setCoverImage(null);
                }
            }

            // Update cover image if provided
            if (item.getCoverImage() != null && !item.getCoverImage().isEmpty()) {
                System.out.println("DEBUG: Setting new cover image: " + item.getCoverImage());
                itemToSave.setCoverImage(item.getCoverImage());
            }

        } else {
            // New item
            item.setId(null);
            itemToSave = item;
        }

        // Extract dynamic attributes
        Map<String, Object> attributes = new HashMap<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("attr_")) {
                attributes.put(entry.getKey().substring(5), entry.getValue());
            }
        }
        itemToSave.setAttributes(attributes);

        System.out.println("DEBUG: Saving item: " + itemToSave);
        itemService.saveItem(itemToSave, files, allParams);

        // Post-save logic: if cover image is null but we have photos, set the first one
        // as cover
        if ((itemToSave.getCoverImage() == null || itemToSave.getCoverImage().isEmpty())
                && !itemToSave.getPhotos().isEmpty()) {
            System.out.println(
                    "DEBUG: Cover image is null, setting default to first photo: " + itemToSave.getPhotos().get(0));
            itemToSave.setCoverImage(itemToSave.getPhotos().get(0));
            itemService.saveItem(itemToSave, null, null); // Resave just to update cover
        }

        return "redirect:/items";
    }

    @GetMapping("/{id}")
    public String viewItem(@PathVariable String id, Model model) {
        model.addAttribute("item", itemService.getItemById(id));
        return "items/view";
    }

    @GetMapping("/edit/{id}")
    public String editItemForm(@PathVariable("id") String id, Model model) {
        System.out.println("Editing item with ID: " + id);
        Item item = itemService.getItemById(id);
        System.out.println("Found item: " + item.getName());
        model.addAttribute("item", item);
        model.addAttribute("categories", categoryRepository.findAll());
        return "items/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable String id) {
        itemService.deleteItem(id);
        return "redirect:/items";
    }
}
