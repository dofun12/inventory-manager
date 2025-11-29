package com.inventory.app.controller;

import com.inventory.app.model.Item;
import com.inventory.app.service.ItemService;
import com.inventory.app.util.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final ItemService itemService;

    public DashboardController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String userId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.isCurrentUserAdmin();

        List<Item> items;
        if (isAdmin) {
            items = itemService.getAllItems();
        } else {
            items = itemService.getItemsByUser(userId);
        }

        model.addAttribute("totalItems", items.size());
        model.addAttribute("recentItems", items.stream().limit(8).toList());
        model.addAttribute("isAdmin", isAdmin);
        return "dashboard";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }
}
