package com.strandls.species.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * One previewed row to save: the values POST /v1/species/batch/preview returned for
 * it, sent back as they are.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpeciesBatchUploadRow {

	private Integer rowNumber;
	private String scientificName;
	private Long speciesId;
	/** The row's fields with their labels; used for the activity entry */
	private List<SpeciesBatchFieldValue> fields = new ArrayList<>();
	/** The create payloads from the preview, one per species field */
	private List<SpeciesFieldUpdateData> fieldPayloads = new ArrayList<>();

	public SpeciesBatchUploadRow() {
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
}
