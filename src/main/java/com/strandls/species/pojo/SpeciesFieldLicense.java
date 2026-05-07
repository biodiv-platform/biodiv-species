/**
 * 
 */
package com.strandls.species.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Abhishek Rudra
 *
 * 
 */

@Entity
@Table(name = "species_field_license")
public class SpeciesFieldLicense implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6930866207113713795L;
	private Long speciesFieldId;
	private Long licenseId;

	/**
	 * 
	 */
	public SpeciesFieldLicense() {
		super();
	}

	/**
	 * @param speciesFieldId
	 * @param licenseId
	 */
	public SpeciesFieldLicense(Long speciesFieldId, Long licenseId) {
		super();
		this.speciesFieldId = speciesFieldId;
		this.licenseId = licenseId;
	}

	@Id
	@Column(name = "species_field_licenses_id")
	public Long getSpeciesFieldId() {
		return speciesFieldId;
	}

	public void setSpeciesFieldId(Long speciesFieldId) {
		this.speciesFieldId = speciesFieldId;
	}

	@Column(name = "license_id")
	public Long getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(Long licenseId) {
		this.licenseId = licenseId;
	}

}
