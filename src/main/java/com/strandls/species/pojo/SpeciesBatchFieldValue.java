package com.strandls.species.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * One species field cell of a batch upload row: which leaf field it fills and the
 * content (HTML or plain text). The label is filled in by the preview.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpeciesBatchFieldValue {

	private Long fieldId;
	private String label;
	private String value;

	public SpeciesBatchFieldValue() {
		super();
	}

	public SpeciesBatchFieldValue(Long fieldId, String label, String value) {
		super();
		this.fieldId = fieldId;
		this.label = label;
		this.value = value;
	}

	public Long getFieldId() {
		return fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
