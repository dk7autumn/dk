package com.dk.salesystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dk.salesystem.dto.ApiResponse;
import com.dk.salesystem.dto.PageResult;
import com.dk.salesystem.entity.Fish;
import com.dk.salesystem.entity.SaleRecord;
import com.dk.salesystem.service.FishService;
import com.dk.salesystem.service.SaleService;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sale")
@PreAuthorize("isAuthenticated()")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @Autowired
    private FishService fishService;

    @GetMapping
    @PreAuthorize("hasAuthority('sale:query')")
    public ApiResponse<PageResult<SaleRecord>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long fishId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<SaleRecord> pageResult = saleService.findPage(page, size, fishId, startDate, endDate);
        return ApiResponse.success(PageResult.of(
            pageResult.getRecords(),
            pageResult.getTotal(),
            (int) pageResult.getCurrent(),
            (int) pageResult.getSize()
        ));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sale:add')")
    public ApiResponse<SaleRecord> add(@RequestBody SaleRecordRequest request) {
        Fish fish = fishService.findById(request.getFishId());
        if (fish == null) {
            throw new RuntimeException("鱼类不存在");
        }

        // 使用请求中的单价，如果没有则使用鱼类当前单价
        BigDecimal unitPrice = request.getUnitPrice() != null ? request.getUnitPrice() : fish.getPrice();
        BigDecimal totalPrice = unitPrice.multiply(request.getQuantity());

        SaleRecord record = new SaleRecord();
        record.setFishId(fish.getId());
        record.setQuantity(request.getQuantity());
        record.setUnitPrice(unitPrice);
        record.setTotalPrice(totalPrice);
        record.setSaleDatetime(request.getSaleDatetime() != null ? request.getSaleDatetime() : LocalDateTime.now());
        record.setRemark(request.getRemark());

        return ApiResponse.success(saleService.save(record));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sale:delete')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        saleService.deleteById(id);
        return ApiResponse.success();
    }

    @GetMapping("/stats/daily")
    @PreAuthorize("hasAuthority('stats:query')")
    public ApiResponse<Map<String, Object>> dailyStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiResponse.success(saleService.getDailyStats(date));
    }

    @GetMapping("/stats/weekly")
    @PreAuthorize("hasAuthority('stats:query')")
    public ApiResponse<Map<String, Object>> weeklyStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiResponse.success(saleService.getWeeklyStats(date));
    }

    @GetMapping("/stats/monthly")
    @PreAuthorize("hasAuthority('stats:query')")
    public ApiResponse<Map<String, Object>> monthlyStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiResponse.success(saleService.getMonthlyStats(date));
    }

    @GetMapping("/stats/quarterly")
    @PreAuthorize("hasAuthority('stats:query')")
    public ApiResponse<Map<String, Object>> quarterlyStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiResponse.success(saleService.getQuarterlyStats(date));
    }

    @GetMapping("/stats/yearly")
    @PreAuthorize("hasAuthority('stats:query')")
    public ApiResponse<Map<String, Object>> yearlyStats(@RequestParam int year) {
        return ApiResponse.success(saleService.getYearlyStats(year));
    }

    // 内部类用于接收请求参数
    public static class SaleRecordRequest {
        private Long fishId;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private String remark;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
        private LocalDateTime saleDatetime;

        public Long getFishId() { return fishId; }
        public void setFishId(Long fishId) { this.fishId = fishId; }
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public LocalDateTime getSaleDatetime() { return saleDatetime; }
        public void setSaleDatetime(LocalDateTime saleDatetime) { this.saleDatetime = saleDatetime; }
    }
}
