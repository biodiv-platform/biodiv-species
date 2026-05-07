/**
 * 
 */
package com.strandls.species.pojo;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * @author Abhishek Rudra
 *
 * 
 */
@Entity
@Table(name = "species_field_contributor")
@IdClass(SpeciesFieldContributorCompositeKey.class)
public class SpeciesFieldContributor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3622651953716416157L;
	private Long speciesFieldId;
	private Long contributorId;

	/**
	 * 
	 */
	public SpeciesFieldContributor() {
		super();
	}

	/**
	 * @param speciesFieldId
	 * @param contributorId
	 */
	public SpeciesFieldContributor(Long speciesFieldId, Long contributorId) {
		super();
		this.speciesFieldId = speciesFieldId;
		this.contributorId = contributorId;
	}

	@Id
	@Column(name = "species_field_attributors_id")
	public Long getSpeciesFieldId() {
		return speciesFieldId;
	}

	public void setSpeciesFieldId(Long speciesFieldId) {
		this.speciesFieldId = speciesFieldId;
	}

	@Id
	@Column(name = "contributor_id")
	public Long getContributorId() {
		return contributorId;
	}

	public void setContributorId(Long contributorId) {
		this.contributorId = contributorId;
	}
}
