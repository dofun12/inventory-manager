package com.inventory.app.controller;

import com.inventory.app.model.Category;
import com.inventory.app.repository.CategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "categories/list";
    }

    @GetMapping("/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/form";
    }

    @PostMapping
    public String saveCategory(@ModelAttribute Category category, @RequestParam Map<String, String> allParams) {
        if (category.getId() != null && category.getId().isEmpty()) {
            category.setId(null);
        }

        // Process dropdown options from comma-separated strings
        if (category.getFieldTemplates() != null) {
            for (int i = 0; i < category.getFieldTemplates().size(); i++) {
                String optionsKey = "fieldTemplates[" + i + "].dropdownOptionsString";
                if (allParams.containsKey(optionsKey)) {
                    String optionsString = allParams.get(optionsKey);
                    if (optionsString != null && !optionsString.trim().isEmpty()) {
                        String[] options = optionsString.split(",");
                        java.util.List<String> optionsList = new java.util.ArrayList<>();
                        for (String option : options) {
                            optionsList.add(option.trim());
                        }
                        category.getFieldTemplates().get(i).setDropdownOptions(optionsList);
                    }
                }
            }
        }

        categoryRepository.save(category);
        return "redirect:/categories";
    }

    // AJAX endpoint for modal
    @PostMapping("/api/add")
    @ResponseBody
    public Category addCategoryApi(@RequestBody Category category) {
        return categoryRepository.save(category);
    }

    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable String id, Model model) {
        model.addAttribute("category", categoryRepository.findById(id).orElseThrow());
        return "categories/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable String id) {
        categoryRepository.deleteById(id);
        return "redirect:/categories";
    }

    // API endpoint to get category field templates
    @GetMapping("/api/{name}/fields")
    @ResponseBody
    public Category getCategoryFields(@PathVariable String name) {
        return categoryRepository.findByName(name).orElse(new Category());
    }
}
