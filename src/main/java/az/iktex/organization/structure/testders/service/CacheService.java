package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.*;
import az.iktex.organization.structure.testders.response.CacheStatsResponse;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final EntityManagerFactory entityManagerFactory;

    public CacheStatsResponse getStats() {
        SessionFactory sessionFactory =
                entityManagerFactory.unwrap(SessionFactory.class);

        Statistics statistics = sessionFactory.getStatistics();

        var regions =
                new LinkedHashMap<String, CacheStatsResponse.RegionStats>();

        for (String regionName : new String[]{
                "courses", "departments", "authors", "books",
                "author-books", "author-authorBooks", "book-authorBooks"
        }) {
            var region = statistics.getDomainDataRegionStatistics(regionName);

            regions.put(regionName, new CacheStatsResponse.RegionStats(
                    region.getHitCount(),
                    region.getMissCount(),
                    region.getPutCount()
            ));
        }

        return new CacheStatsResponse(
                statistics.getSecondLevelCacheHitCount(),
                statistics.getSecondLevelCacheMissCount(),
                statistics.getSecondLevelCachePutCount(),
                statistics.getQueryCacheHitCount(),
                regions
        );
    }

    public void clearCache() {
        entityManagerFactory.unwrap(SessionFactory.class)
                .getCache()
                .evictAllRegions();
    }

    public void resetStats() {
        entityManagerFactory.unwrap(SessionFactory.class)
                .getStatistics()
                .clear();
    }

    public void clearEntityCache(String region) {
        Class<?> entityClass = switch (region) {
            case "courses" -> Course.class;
            case "departments" -> Department.class;
            case "authors" -> Author.class;
            case "books" -> Book.class;
            case "author-books" -> AuthorBook.class;
            default -> throw new IllegalArgumentException(
                    "Unknown entity cache region: " + region
            );
        };

        entityManagerFactory.getCache().evict(entityClass);
    }
}