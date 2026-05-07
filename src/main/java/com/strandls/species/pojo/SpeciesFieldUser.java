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
@Table(name = "species_field_suser")
@IdClass(SpeciesFieldUserCompositeKey.class)
public class SpeciesFieldUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5837443871419071626L;
	private Long speciesFieldId;
	private Long userId;

	/**
	 * 
	 */
	public SpeciesFieldUser() {
		super();
	}

	/**
	 * @param speciesFieldId
	 * @param userId
	 */
	public SpeciesFieldUser(Long speciesFieldId, Long userId) {
		super();
		this.speciesFieldId = speciesFieldId;
		this.userId = userId;
	}

	@Id
	@Column(name = "species_field_contributors_id")
	public Long getSpeciesFieldId() {
		return speciesFieldId;
	}

	public void setSpeciesFieldId(Long speciesFieldId) {
		this.speciesFieldId = speciesFieldId;
	}

	@Id
	@Column(name = "suser_id")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
