package com.dk.salesystem.controller;

import com.dk.salesystem.entity.Fish;
import com.dk.salesystem.entity.SaleRecord;
import com.dk.salesystem.service.FishService;
import com.dk.salesystem.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @Autowired
    private FishService fishService;

    @GetMapping
    public String listSales(Model model) {
        model.addAttribute("saleRecords", saleService.findAll());
        model.addAttribute("fishList", fishService.findAll());
        model.addAttribute("saleRecord", new SaleRecord());
        return "sales/list";
    }

    @PostMapping("/add")
    public String addSale(@RequestParam Long fishId,
                          @RequestParam Integer quantity,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate saleDate) {
        Fish fish = fishService.findById(fishId)
                .orElseThrow(() -> new RuntimeException("Fish not found"));

        SaleRecord record = new SaleRecord();
        record.setFish(fish);
        record.setQuantity(quantity);
        record.setTotalPrice(fish.getPrice().multiply(BigDecimal.valueOf(quantity)));
        record.setSaleDate(saleDate);

        saleService.save(record);
        return "redirect:/sales";
    }

    @GetMapping("/stats")
    public String statsPage() {
        return "stats/index";
    }

    @GetMapping("/stats/daily")
    public String dailyStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Model model) {
        Map<String, Object> stats = saleService.getDailyStats(date);
        model.addAttribute("stats", stats);
        model.addAttribute("statType", "每日");
        model.addAttribute("date", date);
        return "stats/result";
    }

    @GetMapping("/stats/weekly")
    public String weeklyStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Model model) {
        Map<String, Object> stats = saleService.getWeeklyStats(date);
        model.addAttribute("stats", stats);
        model.addAttribute("statType", "每周");
        model.addAttribute("date", date);
        return "stats/result";
    }

    @GetMapping("/stats/monthly")
    public String monthlyStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Model model) {
        Map<String, Object> stats = saleService.getMonthlyStats(date);
        model.addAttribute("stats", stats);
        model.addAttribute("statType", "每月");
        model.addAttribute("date", date);
        return "stats/result";
    }

    @GetMapping("/stats/quarterly")
    public String quarterlyStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Model model) {
        Map<String, Object> stats = saleService.getQuarterlyStats(date);
        model.addAttribute("stats", stats);
        model.addAttribute("statType", "每季度");
        model.addAttribute("date", date);
        return "stats/result";
    }

    @GetMapping("/stats/yearly")
    public String yearlyStats(@RequestParam int year, Model model) {
        Map<String, Object> stats = saleService.getYearlyStats(year);
        model.addAttribute("stats", stats);
        model.addAttribute("statType", "每年");
        model.addAttribute("date", year);
        return "stats/result";
    }
}
