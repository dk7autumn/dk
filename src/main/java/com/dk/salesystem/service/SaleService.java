package com.dk.salesystem.service;

import com.dk.salesystem.entity.SaleRecord;
import com.dk.salesystem.mapper.SaleRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaleService {

    @Autowired
    private SaleRecordMapper saleRecordMapper;

    public List<SaleRecord> findAll() {
        return saleRecordMapper.selectList(null);
    }

    public List<SaleRecord> findByDate(LocalDate date) {
        return saleRecordMapper.findBySaleDate(date);
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
        List<SaleRecord> records = saleRecordMapper.findBySaleDate(date);
        return calculateStats(records);
    }

    public Map<String, Object> getWeeklyStats(LocalDate date) {
        LocalDate startOfWeek = date.minusDays(date.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        List<SaleRecord> records = saleRecordMapper.findBySaleDateBetween(startOfWeek, endOfWeek);
        Map<String, Object> stats = calculateStats(records);
        stats.put("startDate", startOfWeek);
        stats.put("endDate", endOfWeek);
        return stats;
    }

    public Map<String, Object> getMonthlyStats(LocalDate date) {
        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        List<SaleRecord> records = saleRecordMapper.findBySaleDateBetween(startOfMonth, endOfMonth);
        Map<String, Object> stats = calculateStats(records);
        stats.put("startDate", startOfMonth);
        stats.put("endDate", endOfMonth);
        return stats;
    }

    public Map<String, Object> getQuarterlyStats(LocalDate date) {
        int month = date.getMonthValue();
        int quarter = (month - 1) / 3;
        int startMonth = quarter * 3 + 1;
        LocalDate startOfQuarter = date.withMonth(startMonth).withDayOfMonth(1);
        LocalDate endOfQuarter = startOfQuarter.plusMonths(3).minusDays(1);
        List<SaleRecord> records = saleRecordMapper.findBySaleDateBetween(startOfQuarter, endOfQuarter);
        Map<String, Object> stats = calculateStats(records);
        stats.put("startDate", startOfQuarter);
        stats.put("endDate", endOfQuarter);
        stats.put("quarter", quarter + 1);
        return stats;
    }

    public Map<String, Object> getYearlyStats(int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        List<SaleRecord> records = saleRecordMapper.findBySaleDateBetween(startOfYear, endOfYear);
        Map<String, Object> stats = calculateStats(records);
        stats.put("year", year);
        return stats;
    }

    private Map<String, Object> calculateStats(List<SaleRecord> records) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("records", records);
        stats.put("totalRecords", records.size());

        int totalQuantity = records.stream().mapToInt(SaleRecord::getQuantity).sum();
        BigDecimal totalAmount = records.stream()
                .map(SaleRecord::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("totalQuantity", totalQuantity);
        stats.put("totalAmount", totalAmount);

        return stats;
    }
}
