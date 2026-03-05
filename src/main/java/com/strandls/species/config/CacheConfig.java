package com.strandls.species.config;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.strandls.species.pojo.ShowSpeciesPage;

/**
 * Cache configuration for species data
 * Uses Caffeine for high-performance in-memory caching
 *
 * @author Optimization Team
 */
@Singleton
public class CacheConfig {

	private final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

	private final Cache<String, ShowSpeciesPage> speciesPageCache;

	public CacheConfig() {
		this.speciesPageCache = Caffeine.newBuilder()
				.maximumSize(1000) // Cache up to 1000 species pages
				.expireAfterWrite(30, TimeUnit.MINUTES) // Expire after 30 minutes
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
	 * @param speciesId Species ID
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
	 * Invalidate all cache entries for a specific species
	 * This includes entries with and without user group filtering
	 *
	 * @param speciesId Species ID to invalidate
	 */
	public void invalidateSpeciesCache(Long speciesId) {
		// Invalidate all cache entries for this species (with or without userGroup)
		String prefix = "species:" + speciesId;
		long invalidatedCount = speciesPageCache.asMap().keySet().stream()
				.filter(key -> key.startsWith(prefix))
				.peek(key -> speciesPageCache.invalidate(key))
				.count();

		if (invalidatedCount > 0) {
			logger.info("Invalidated {} cache entries for species: {}", invalidatedCount, speciesId);
		}
	}

	/**
	 * Get cache statistics for monitoring
	 *
	 * @return Cache stats string
	 */
	public String getCacheStats() {
		return speciesPageCache.stats().toString();
	}
}
