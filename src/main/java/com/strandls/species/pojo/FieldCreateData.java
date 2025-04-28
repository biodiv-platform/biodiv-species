package com.strandls.species.pojo;

import java.util.List;

public class FieldCreateData {
	private String header; // The actual field name entered by user
	private Long parentId; // null for "No Parent (Top level)"
	private Long displayOrder;
	private List<FieldHeaderData> translations; // List of translations

	// Fields from FieldHeader
	private String description;
	private String urlIdentifier;
	private Long languageId;

	// Default constructor
	public FieldCreateData() {
		super();
	}

	// Constructor with all fields
	public FieldCreateData(String header, Long parentId, Long displayOrder, String description, String urlIdentifier,
			Long languageId) {
		this.header = header;
		this.parentId = parentId;
		this.displayOrder = displayOrder;
		this.description = description;
		this.urlIdentifier = urlIdentifier;
		this.languageId = languageId;
	}

	// Getters and setters
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Long displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrlIdentifier() {
		return urlIdentifier;
	}

	public void setUrlIdentifier(String urlIdentifier) {
		this.urlIdentifier = urlIdentifier;
	}

	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	public List<FieldHeaderData> getTranslations() {
		return translations;
	}

	public void setTranslations(List<FieldHeaderData> translations) {
		this.translations = translations;
	}
}