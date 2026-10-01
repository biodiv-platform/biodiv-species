package com.strandls.species.config;

import jakarta.inject.Singleton;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.strandls.species.pojo.ShowSpeciesPage;

/**
 * Cache configuration for species data Uses Caffeine for high-performance
 * in-memory caching
 *
 * @author Optimization Team
 */
@Singleton
public class CacheConfig {

	private final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

	private final Cache<String, ShowSpeciesPage> speciesPageCache;

	public CacheConfig() {
		this.speciesPageCache = Caffeine.newBuilder().maximumSize(20000) // Cache up to 20f000 species pages
				.recordStats() // Enable statistics for monitoring
				.build();

		logger.info("Species page cache initialized with max size: 1000, TTL: 30 minutes");
	}

	/**
	 * Get the species page cache instance
	 *
	 * @return Cache instance
	 */
	public Cache<String, ShowSpeciesPage> getSpeciesPageCache() {
		return speciesPageCache;
	}

	/**
	 * Generate cache key for species page
	 *
	 * @param speciesId   Species ID
	 * @param userGroupId User Group ID (can be null)
	 * @return Cache key string
	 */
	public String generateCacheKey(Long speciesId, Long userGroupId) {
		if (userGroupId != null) {
			return "species:" + speciesId + ":ug:" + userGroupId;
		}
		return "species:" + speciesId;
	}

	/**
	 * Invalidate all cache entries for a specific species This includes entries
	 * with and without user group filtering
	 *
	 * @param speciesId Species ID to invalidate
	 */
	public void invalidateSpeciesCache(Long speciesId) {
		// Invalidate all cache entries for this species (with or without userGroup)
		String prefix = "species:" + speciesId;
		long invalidatedCount = speciesPageCache.asMap().keySet().stream().filter(key -> key.startsWith(prefix))
				.peek(key -> speciesPageCache.invalidate(key)).count();

		if (invalidatedCount > 0) {
			logger.info("Invalidated {} cache entries for species: {}", invalidatedCount, speciesId);
		}
	}
	
	public void invalidateSpeciesCache(List<Long> speciesIds) {
	    if (speciesIds == null || speciesIds.isEmpty()) {
	        return;
	    }

	    Set<String> baseKeys = speciesIds.stream()
	            .filter(Objects::nonNull)
	            .map(id -> "species:" + id)
	            .collect(Collectors.toSet());

	    List<String> keysToInvalidate = speciesPageCache.asMap().keySet().stream()
	            .filter(key -> matchesAny(key, baseKeys))
	            .collect(Collectors.toList());

	    speciesPageCache.invalidateAll(keysToInvalidate);

	    if (!keysToInvalidate.isEmpty()) {
	        logger.info("Invalidated {} cache entries for species: {}", keysToInvalidate.size(), speciesIds);
	    }
	}

	private boolean matchesAny(String key, Set<String> baseKeys) {
	    if (baseKeys.contains(key)) {
	        return true;
	    }
	    int idx = key.indexOf(':', "species:".length());
	    return idx > 0 && baseKeys.contains(key.substring(0, idx));
	}

	/**
	 * Get cache statistics for monitoring
	 *
	 * @return Cache stats object with detailed metrics
	 */
	public com.strandls.species.pojo.CacheStats getCacheStats() {
		com.github.benmanes.caffeine.cache.stats.CacheStats stats = speciesPageCache.stats();

		Long currentSize = speciesPageCache.estimatedSize();
		Long hitCount = stats.hitCount();
		Long missCount = stats.missCount();
		Double hitRatio = stats.hitRate();
		Long evictionCount = stats.evictionCount();

		return new com.strandls.species.pojo.CacheStats(currentSize, hitCount, missCount, hitRatio, evictionCount);
	}
}
