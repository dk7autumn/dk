package com.dk.salesystem.controller;

import com.dk.salesystem.dto.ApiResponse;
import com.dk.salesystem.entity.Fish;
import com.dk.salesystem.service.FishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fish")
@PreAuthorize("isAuthenticated()")
public class FishController {

    @Autowired
    private FishService fishService;

    @GetMapping
    @PreAuthorize("hasAuthority('fish:query')")
    public ApiResponse<List<Fish>> list() {
        return ApiResponse.success(fishService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('fish:query')")
    public ApiResponse<Fish> getById(@PathVariable Long id) {
        Fish fish = fishService.findById(id);
        if (fish == null) {
            return ApiResponse.error("鱼类不存在");
        }
        return ApiResponse.success(fish);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('fish:add')")
    public ApiResponse<Fish> add(@RequestBody Fish fish) {
        return ApiResponse.success(fishService.save(fish));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('fish:edit')")
    public ApiResponse<Fish> update(@PathVariable Long id, @RequestBody Fish fish) {
        fish.setId(id);
        return ApiResponse.success(fishService.save(fish));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('fish:delete')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        fishService.deleteById(id);
        return ApiResponse.success();
    }
}
