package com.dk.salesystem.controller;

import com.dk.salesystem.entity.Fish;
import com.dk.salesystem.service.FishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/fish")
public class FishController {

    @Autowired
    private FishService fishService;

    @GetMapping
    public String listFish(Model model) {
        model.addAttribute("fishList", fishService.findAll());
        model.addAttribute("fish", new Fish());
        return "fish/list";
    }

    @PostMapping("/add")
    public String addFish(@ModelAttribute Fish fish) {
        fishService.save(fish);
        return "redirect:/fish";
    }

    @GetMapping("/edit/{id}")
    public String editFish(@PathVariable Long id, Model model) {
        fishService.findById(id).ifPresent(fish -> {
            model.addAttribute("fish", fish);
            model.addAttribute("fishList", fishService.findAll());
        });
        return "fish/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteFish(@PathVariable Long id) {
        fishService.deleteById(id);
        return "redirect:/fish";
    }
}
