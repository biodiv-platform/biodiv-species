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

	private Boolean isEdit;
	private Long fieldId;
	private Long speciesFieldId;
	private Long licenseId;
//	----Core SpeciesField Data------
	private String sfDescription;
	private String sfStatus;
//	-------Attribution Data-----
	private String attributions;
//	-------SpeciesField Resource--------
	private List<SpeciesResourceData> speciesFieldResource;
//	-------COntributor Ids--------------
	private List<Long> contributorIds;
	private List<Reference> references;

	/**
	* 
	*/
	public SpeciesFieldUpdateData() {
		super();
	}

	/**
	 * @param isEdit
	 * @param fieldId
	 * @param speciesFieldId
	 * @param licenseId
	 * @param sfDescription
	 * @param sfStatus
	 * @param attributions
	 * @param speciesFieldResource
	 * @param contributorIds
	 * @param references
	 */
	public SpeciesFieldUpdateData(Boolean isEdit, Long fieldId, Long speciesFieldId, Long licenseId,
			String sfDescription, String sfStatus, String attributions, List<SpeciesResourceData> speciesFieldResource,
			List<Long> contributorIds, List<Reference> references) {
		super();
		this.isEdit = isEdit;
		this.fieldId = fieldId;
		this.speciesFieldId = speciesFieldId;
		this.licenseId = licenseId;
		this.sfDescription = sfDescription;
		this.sfStatus = sfStatus;
		this.attributions = attributions;
		this.speciesFieldResource = speciesFieldResource;
		this.contributorIds = contributorIds;
		this.references = references;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

	public Long getFieldId() {
		return fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	public Long getSpeciesFieldId() {
		return speciesFieldId;
	}

	public void setSpeciesFieldId(Long speciesFieldId) {
		this.speciesFieldId = speciesFieldId;
	}

	public Long getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(Long licenseId) {
		this.licenseId = licenseId;
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

	public List<Long> getContributorIds() {
		return contributorIds;
	}

	public void setContributorIds(List<Long> contributorIds) {
		this.contributorIds = contributorIds;
	}

	public List<Reference> getReferences() {
		return references;
	}

	public void setReferences(List<Reference> references) {
		this.references = references;
	}

}
