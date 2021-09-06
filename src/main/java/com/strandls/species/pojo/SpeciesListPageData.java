/**
 * 
 */
package com.strandls.species.pojo;

import java.util.List;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesListPageData {

	private Long totalCount;
	private List<SpeciesListTiles> speciesTiles;

	/**
	 * 
	 */
	public SpeciesListPageData() {
		super();
	}

	/**
	 * @param totalCount
	 * @param speciesTiles
	 */
	public SpeciesListPageData(Long totalCount, List<SpeciesListTiles> speciesTiles) {
		super();
		this.totalCount = totalCount;
		this.speciesTiles = speciesTiles;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public List<SpeciesListTiles> getSpeciesTiles() {
		return speciesTiles;
	}

	public void setSpeciesTiles(List<SpeciesListTiles> speciesTiles) {
		this.speciesTiles = speciesTiles;
	}

}
