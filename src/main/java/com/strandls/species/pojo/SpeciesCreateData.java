/**
 * 
 */
package com.strandls.species.pojo;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesCreateData {

	private Long taxonConceptId;
	private String title;
	private Long habitatId;

	/**
	 * 
	 */
	public SpeciesCreateData() {
		super();
	}

	/**
	 * @param taxonConceptId
	 * @param title
	 * @param habitatId
	 */
	public SpeciesCreateData(Long taxonConceptId, String title, Long habitatId) {
		super();
		this.taxonConceptId = taxonConceptId;
		this.title = title;
		this.habitatId = habitatId;
	}

	public Long getTaxonConceptId() {
		return taxonConceptId;
	}

	public void setTaxonConceptId(Long taxonConceptId) {
		this.taxonConceptId = taxonConceptId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getHabitatId() {
		return habitatId;
	}

	public void setHabitatId(Long habitatId) {
		this.habitatId = habitatId;
	}

}
