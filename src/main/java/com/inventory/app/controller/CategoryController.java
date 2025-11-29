package com.inventory.app.controller;

import com.inventory.app.model.Category;
import com.inventory.app.repository.CategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String saveCategory(@ModelAttribute Category category) {
        if (category.getId() != null && category.getId().isEmpty()) {
            category.setId(null);
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
}
