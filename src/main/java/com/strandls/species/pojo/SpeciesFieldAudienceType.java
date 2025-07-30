/**
 * 
 */
package com.strandls.species.pojo;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Abhishek Rudra
 *
 * 
 */

@Entity
@Table(name = "species_field_audience_types")
public class SpeciesFieldAudienceType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7126866700045875951L;
	private Long speciesFieldId;
	private String audienceType;

	@Id
	@Column(name = "species_field_id")
	public Long getSpeciesFieldId() {
		return speciesFieldId;
	}

	public void setSpeciesFieldId(Long speciesFieldId) {
		this.speciesFieldId = speciesFieldId;
	}

	@Column(name = "species_field$audience_type")
	public String getAudienceType() {
		return audienceType;
	}

	public void setAudienceType(String audienceType) {
		this.audienceType = audienceType;
	}

}
