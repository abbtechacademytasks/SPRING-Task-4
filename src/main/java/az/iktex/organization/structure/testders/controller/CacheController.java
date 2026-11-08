package az.iktex.organization.structure.testders.controller;

import io.swagger.v3.oas.annotations.tags.Tags;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import az.iktex.organization.structure.testders.response.CacheStatsResponse;
import az.iktex.organization.structure.testders.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tags({@Tag(name = "Course"), @Tag(name = "Department"), @Tag(name = "Author"), @Tag(name = "Book")})
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @Operation(
            summary = "Get shared cache statistics",
            description = "Returns cache statistics for courses, departments, authors and books."
    )
    @GetMapping("/stats")
    public CacheStatsResponse getStats() {
        return cacheService.getStats();
    }

    @Operation(
            summary = "Clear shared caches",
            description = "Evicts all Hibernate cache regions."
    )
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCache() {
        cacheService.clearCache();
    }

    @Operation(
            summary = "Reset cache statistics",
            description = "Resets Hibernate statistics counters."
    )
    @DeleteMapping("/stats")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetStats() {
        cacheService.resetStats();
    }

    @Operation(
            summary = "Clear entity cache",
            description = "Unknown region returns 400 (ERR-1012)."
    )
    @DeleteMapping("/{region}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearEntityCache(
            @PathVariable("region") String region
    ) {
        cacheService.clearEntityCache(region);
    }
}