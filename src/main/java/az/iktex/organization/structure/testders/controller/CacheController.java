package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.response.CacheStatsResponse;
import az.iktex.organization.structure.testders.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @GetMapping("/stats")
    public CacheStatsResponse getStats() {
        return cacheService.getStats();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCache() {
        cacheService.clearCache();
    }

    @DeleteMapping("/stats")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetStats() {
        cacheService.resetStats();
    }

    @DeleteMapping("/{region}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearEntityCache(
            @PathVariable("region") String region
    ) {
        cacheService.clearEntityCache(region);
    }
}