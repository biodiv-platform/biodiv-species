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
public class SpeciesFieldUpdateData {

	private Long speciesFieldId;
//	----Core SpeciesField Data------
	private String sfDescription;
	private String sfStatus;
//	-------Attribution Data-----
	private String attributions;
//	-------SpeciesField Resource--------
	private List<SpeciesResourceData> speciesFieldResource;

	/**
	* 
	*/
	public SpeciesFieldUpdateData() {
		super();
	}

	/**
	 * @param speciesFieldId
	 * @param sfDescription
	 * @param sfStatus
	 * @param attributions
	 * @param speciesFieldResource
	 */
	public SpeciesFieldUpdateData(Long speciesFieldId, String sfDescription, String sfStatus, String attributions,
			List<SpeciesResourceData> speciesFieldResource) {
		super();
		this.speciesFieldId = speciesFieldId;
		this.sfDescription = sfDescription;
		this.sfStatus = sfStatus;
		this.attributions = attributions;
		this.speciesFieldResource = speciesFieldResource;
	}

	public Long getSpeciesFieldId() {
		return speciesFieldId;
	}

	public void setSpeciesFieldId(Long speciesFieldId) {
		this.speciesFieldId = speciesFieldId;
	}

	public String getSfDescription() {
		return sfDescription;
	}

	public void setSfDescription(String sfDescription) {
		this.sfDescription = sfDescription;
	}

	public String getSfStatus() {
		return sfStatus;
	}

	public void setSfStatus(String sfStatus) {
		this.sfStatus = sfStatus;
	}

	public String getAttributions() {
		return attributions;
	}

	public void setAttributions(String attributions) {
		this.attributions = attributions;
	}

	public List<SpeciesResourceData> getSpeciesFieldResource() {
		return speciesFieldResource;
	}

	public void setSpeciesFieldResource(List<SpeciesResourceData> speciesFieldResource) {
		this.speciesFieldResource = speciesFieldResource;
	}

}
