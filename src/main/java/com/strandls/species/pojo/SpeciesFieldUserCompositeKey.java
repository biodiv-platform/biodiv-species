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
public class SpeciesFieldUserCompositeKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4919834426273204660L;
	private Long speciesFieldId;
	private Long userId;

	public Long getSpeciesFieldId() {
		return speciesFieldId;
	}

	public void setSpeciesFieldId(Long speciesFieldId) {
		this.speciesFieldId = speciesFieldId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
