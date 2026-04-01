package com.dk.salesystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dk.salesystem.entity.Fish;
import com.dk.salesystem.entity.SaleRecord;
import com.dk.salesystem.mapper.FishMapper;
import com.dk.salesystem.mapper.SaleRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SaleService {

    @Autowired
    private SaleRecordMapper saleRecordMapper;

    @Autowired
    private FishMapper fishMapper;

    public List<SaleRecord> findAll() {
        return saleRecordMapper.selectList(null);
    }

    public Page<SaleRecord> findPage(int page, int size, Long fishId, LocalDate startDate, LocalDate endDate) {
        Page<SaleRecord> pageParam = new Page<>(page, size);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SaleRecord> wrapper =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();

        if (fishId != null) {
            wrapper.eq("fish_id", fishId);
        }
        if (startDate != null) {
            wrapper.ge("sale_datetime", startDate);
        }
        if (endDate != null) {
            wrapper.le("sale_datetime", endDate);
        }

        Page<SaleRecord> pageResult = saleRecordMapper.selectPage(pageParam, wrapper);
        // 关联鱼类信息
        List<SaleRecord> records = pageResult.getRecords();
        if (!records.isEmpty()) {
            List<Long> fishIds = records.stream().map(SaleRecord::getFishId).distinct().collect(Collectors.toList());
            List<Fish> fishList = fishMapper.selectBatchIds(fishIds);
            Map<Long, Fish> fishMap = fishList.stream().collect(Collectors.toMap(Fish::getId, f -> f));
            for (SaleRecord record : records) {
                record.setFish(fishMap.get(record.getFishId()));
            }
        }
        return pageResult;
    }

    public SaleRecord save(SaleRecord record) {
        if (record.getId() == null) {
            saleRecordMapper.insert(record);
        } else {
            saleRecordMapper.updateById(record);
        }
        return record;
    }

    public void deleteById(Long id) {
        saleRecordMapper.deleteById(id);
    }

    public Map<String, Object> getDailyStats(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        List<SaleRecord> records = saleRecordMapper.findBySaleDateTimeBetween(startOfDay, endOfDay);
        return calculateStats(records);
    }

    public Map<String, Object> getWeeklyStats(LocalDate date) {
        LocalDateTime startOfWeek = date.minusDays(date.getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(7).minusSeconds(1);
        List<SaleRecord> records = saleRecordMapper.findBySaleDateTimeBetween(startOfWeek, endOfWeek);
        Map<String, Object> stats = calculateStats(records);
        stats.put("startDate", startOfWeek.toLocalDate());
        stats.put("endDate", endOfWeek.toLocalDate());
        return stats;
    }

    public Map<String, Object> getMonthlyStats(LocalDate date) {
        LocalDateTime startOfMonth = date.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        List<SaleRecord> records = saleRecordMapper.findBySaleDateTimeBetween(startOfMonth, endOfMonth);
        Map<String, Object> stats = calculateStats(records);
        stats.put("startDate", startOfMonth.toLocalDate());
        stats.put("endDate", endOfMonth.toLocalDate());
        return stats;
    }

    public Map<String, Object> getQuarterlyStats(LocalDate date) {
        int month = date.getMonthValue();
        int quarter = (month - 1) / 3;
        int startMonth = quarter * 3 + 1;
        LocalDateTime startOfQuarter = date.withMonth(startMonth).withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfQuarter = startOfQuarter.plusMonths(3).minusSeconds(1);
        List<SaleRecord> records = saleRecordMapper.findBySaleDateTimeBetween(startOfQuarter, endOfQuarter);
        Map<String, Object> stats = calculateStats(records);
        stats.put("startDate", startOfQuarter.toLocalDate());
        stats.put("endDate", endOfQuarter.toLocalDate());
        stats.put("quarter", quarter + 1);
        return stats;
    }

    public Map<String, Object> getYearlyStats(int year) {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        List<SaleRecord> records = saleRecordMapper.findBySaleDateTimeBetween(startOfYear, endOfYear);
        Map<String, Object> stats = calculateStats(records);
        stats.put("year", year);
        return stats;
    }

    private Map<String, Object> calculateStats(List<SaleRecord> records) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("records", records);
        stats.put("totalRecords", records.size());

        BigDecimal totalQuantity = records.stream()
                .map(SaleRecord::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = records.stream()
                .map(SaleRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("totalQuantity", totalQuantity);
        stats.put("totalAmount", totalAmount);

        return stats;
    }
}
