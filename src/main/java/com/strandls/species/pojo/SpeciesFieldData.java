/**
 * 
 */
package com.strandls.species.pojo;

import java.util.List;

import com.strandls.resource.pojo.License;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.user.pojo.UserIbp;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesFieldData {

	private Long id;
	private Long fieldId;
	private Long displayOrder;
	private String label;
	private String fieldDescription;
	private String header;
	private SpeciesField fieldData;
	private List<Reference> references;
	private String attributions;
	private List<UserIbp> contributor;
	private String audienceType;
	private License license;
	private List<ResourceData> speciesFieldResource;
	private String path;

	/**
	 * 
	 */
	public SpeciesFieldData() {
		super();
	}

	/**
	 * @param id
	 * @param fieldId
	 * @param displayOrder
	 * @param label
	 * @param fieldDescription
	 * @param header
	 * @param fieldData
	 * @param references
	 * @param attributions
	 * @param contributor
	 * @param audienceType
	 * @param license
	 * @param speciesFieldResource
	 */
	public SpeciesFieldData(Long id, Long fieldId, Long displayOrder, String label, String fieldDescription,
			String header, SpeciesField fieldData, List<Reference> references, String attributions,
			List<UserIbp> contributor, String audienceType, License license, List<ResourceData> speciesFieldResource,String path) {
		super();
		this.id = id;
		this.fieldId = fieldId;
		this.displayOrder = displayOrder;
		this.label = label;
		this.fieldDescription = fieldDescription;
		this.header = header;
		this.fieldData = fieldData;
		this.references = references;
		this.attributions = attributions;
		this.contributor = contributor;
		this.audienceType = audienceType;
		this.license = license;
		this.speciesFieldResource = speciesFieldResource;
		this.path = path;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFieldId() {
		return fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	public Long getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Long displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getFieldDescription() {
		return fieldDescription;
	}

	public void setFieldDescription(String fieldDescription) {
		this.fieldDescription = fieldDescription;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public SpeciesField getFieldData() {
		return fieldData;
	}

	public void setFieldData(SpeciesField fieldData) {
		this.fieldData = fieldData;
	}

	public List<Reference> getReferences() {
		return references;
	}

	public void setReferences(List<Reference> references) {
		this.references = references;
	}

	public String getAttributions() {
		return attributions;
	}

	public void setAttributions(String attributions) {
		this.attributions = attributions;
	}

	public List<UserIbp> getContributor() {
		return contributor;
	}

	public void setContributor(List<UserIbp> contributor) {
		this.contributor = contributor;
	}

	public String getAudienceType() {
		return audienceType;
	}

	public void setAudienceType(String audienceType) {
		this.audienceType = audienceType;
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	public List<ResourceData> getSpeciesFieldResource() {
		return speciesFieldResource;
	}

	public void setSpeciesFieldResource(List<ResourceData> speciesFieldResource) {
		this.speciesFieldResource = speciesFieldResource;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
