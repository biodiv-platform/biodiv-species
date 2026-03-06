package com.strandls.species.pojo;

/**
 * Cache statistics response object
 *
 * @author Optimization Team
 */
public class CacheStats {

	private Long currentSize;
	private Long hitCount;
	private Long missCount;
	private Double hitRatio;
	private Long evictionCount;

	public CacheStats() {
		super();
	}

	public CacheStats(Long currentSize, Long hitCount, Long missCount, Double hitRatio, Long evictionCount) {
		super();
		this.currentSize = currentSize;
		this.hitCount = hitCount;
		this.missCount = missCount;
		this.hitRatio = hitRatio;
		this.evictionCount = evictionCount;
	}

	public Long getCurrentSize() {
		return currentSize;
	}

	public void setCurrentSize(Long currentSize) {
		this.currentSize = currentSize;
	}

	public Long getHitCount() {
		return hitCount;
	}

	public void setHitCount(Long hitCount) {
		this.hitCount = hitCount;
	}

	public Long getMissCount() {
		return missCount;
	}

	public void setMissCount(Long missCount) {
		this.missCount = missCount;
	}

	public Double getHitRatio() {
		return hitRatio;
	}

	public void setHitRatio(Double hitRatio) {
		this.hitRatio = hitRatio;
	}

	public Long getEvictionCount() {
		return evictionCount;
	}

	public void setEvictionCount(Long evictionCount) {
		this.evictionCount = evictionCount;
	}

}
