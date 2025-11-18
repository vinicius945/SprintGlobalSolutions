package com.fiap.gestaoltakn.controller.cache;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cache")
public class CacheStatsController {

    private final CacheManager cacheManager;

    public CacheStatsController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping("/stats")
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Estatísticas do cache de departamentos
            CaffeineCache departamentosCache = (CaffeineCache) cacheManager.getCache("departamentos");
            if (departamentosCache != null) {
                CacheStats departamentosStats = departamentosCache.getNativeCache().stats();
                stats.put("departamentos", Map.of(
                        "hitCount", departamentosStats.hitCount(),
                        "missCount", departamentosStats.missCount(),
                        "loadSuccessCount", departamentosStats.loadSuccessCount(),
                        "loadFailureCount", departamentosStats.loadFailureCount(),
                        "totalLoadTime", departamentosStats.totalLoadTime(),
                        "evictionCount", departamentosStats.evictionCount(),
                        "hitRate", String.format("%.2f", departamentosStats.hitRate()),
                        "missRate", String.format("%.2f", departamentosStats.missRate())
                ));
            }

            // Estatísticas do cache de funcionários
            CaffeineCache funcionariosCache = (CaffeineCache) cacheManager.getCache("funcionarios");
            if (funcionariosCache != null) {
                CacheStats funcionariosStats = funcionariosCache.getNativeCache().stats();
                stats.put("funcionarios", Map.of(
                        "hitCount", funcionariosStats.hitCount(),
                        "missCount", funcionariosStats.missCount(),
                        "loadSuccessCount", funcionariosStats.loadSuccessCount(),
                        "loadFailureCount", funcionariosStats.loadFailureCount(),
                        "totalLoadTime", funcionariosStats.totalLoadTime(),
                        "evictionCount", funcionariosStats.evictionCount(),
                        "hitRate", String.format("%.2f", funcionariosStats.hitRate()),
                        "missRate", String.format("%.2f", funcionariosStats.missRate())
                ));
            }

            stats.put("status", "success");
            stats.put("message", "Cache statistics retrieved successfully");

        } catch (Exception e) {
            stats.put("status", "error");
            stats.put("message", "Error retrieving cache stats: " + e.getMessage());
        }

        return stats;
    }

    @GetMapping("/clear")
    public Map<String, String> clearAllCaches() {
        Map<String, String> response = new HashMap<>();
        try {
            cacheManager.getCacheNames().forEach(cacheName -> {
                cacheManager.getCache(cacheName).clear();
                System.out.println("Cache cleared: " + cacheName);
            });
            response.put("status", "success");
            response.put("message", "All caches cleared successfully!");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error clearing caches: " + e.getMessage());
        }
        return response;
    }

}
