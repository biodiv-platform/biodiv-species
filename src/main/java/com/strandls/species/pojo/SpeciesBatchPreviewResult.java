package com.strandls.species.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Preview of one batch upload row: every value resolved to what will be saved, plus
 * the ready-to-send create payloads (one per species field) for
 * PUT /v1/species/update/speciesField/{speciesId}.
 *
 * A row with errors must not be uploaded; warnings are informational.
 */
public class SpeciesBatchPreviewResult {

	private Integer rowNumber;
	private String scientificName;
	private Long speciesId;
	private List<SpeciesBatchContributor> contributors = new ArrayList<>();
	private String attributions;
	private Long licenseId;
	private String licenseName;
	private Long languageId;
	private String languageName;
	private String status;
	private List<Reference> references = new ArrayList<>();
	private List<SpeciesBatchFieldValue> fields = new ArrayList<>();
	private List<SpeciesFieldUpdateData> fieldPayloads = new ArrayList<>();
	private List<String> errors = new ArrayList<>();
	private List<String> warnings = new ArrayList<>();

	public SpeciesBatchPreviewResult() {
		super();
	}

	public Integer getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public Long getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(Long speciesId) {
		this.speciesId = speciesId;
	}

	public List<SpeciesBatchContributor> getContributors() {
		return contributors;
	}

	public void setContributors(List<SpeciesBatchContributor> contributors) {
		this.contributors = contributors;
	}

	public String getAttributions() {
		return attributions;
	}

	public void setAttributions(String attributions) {
		this.attributions = attributions;
	}

	public Long getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(Long licenseId) {
		this.licenseId = licenseId;
	}

	public String getLicenseName() {
		return licenseName;
	}

	public void setLicenseName(String licenseName) {
		this.licenseName = licenseName;
	}

	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Reference> getReferences() {
		return references;
	}

	public void setReferences(List<Reference> references) {
		this.references = references;
	}

	public List<SpeciesBatchFieldValue> getFields() {
		return fields;
	}

	public void setFields(List<SpeciesBatchFieldValue> fields) {
		this.fields = fields;
	}

	public List<SpeciesFieldUpdateData> getFieldPayloads() {
		return fieldPayloads;
	}

	public void setFieldPayloads(List<SpeciesFieldUpdateData> fieldPayloads) {
		this.fieldPayloads = fieldPayloads;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}
}
