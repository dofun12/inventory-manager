package com.inventory.app.controller;

import com.inventory.app.repository.ItemRepository;
import com.inventory.app.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ItemService itemService;

    public DashboardController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalItems", itemService.getAllItems().size());
        // Get recent items (simplified: just first 8 for now)
        model.addAttribute("recentItems", itemService.getAllItems().stream().limit(8).toList());
        return "dashboard";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }
}
