/**
 * 
 */
package com.strandls.species.pojo;

import java.io.Serializable;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesFieldContributorCompositeKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5317303954938127920L;
	private Long speciesFieldId;
	private Long contributorId;

	public Long getSpeciesFieldId() {
		return speciesFieldId;
	}

	public void setSpeciesFieldId(Long speciesFieldId) {
		this.speciesFieldId = speciesFieldId;
	}

	public Long getContributorId() {
		return contributorId;
	}

	public void setContributorId(Long contributorId) {
		this.contributorId = contributorId;
	}
}
