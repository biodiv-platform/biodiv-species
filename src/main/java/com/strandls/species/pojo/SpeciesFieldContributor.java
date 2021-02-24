/**
 * 
 */
package com.strandls.species.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

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
	private Long attributorsId;

	/**
	 * 
	 */
	public SpeciesFieldContributor() {
		super();
	}

	/**
	 * @param speciesFieldId
	 * @param contributorId
	 * @param attributorsId
	 */
	public SpeciesFieldContributor(Long speciesFieldId, Long contributorId, Long attributorsId) {
		super();
		this.speciesFieldId = speciesFieldId;
		this.contributorId = contributorId;
		this.attributorsId = attributorsId;
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

	@Id
	@Column(name = "attributors_idx")
	public Long getAttributorsId() {
		return attributorsId;
	}

	public void setAttributorsId(Long attributorsId) {
		this.attributorsId = attributorsId;
	}

}
