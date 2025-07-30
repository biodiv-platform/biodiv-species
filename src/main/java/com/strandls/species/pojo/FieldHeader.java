/**
 * 
 */
package com.strandls.species.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Abhishek Rudra
 *
 * 
 */

@Entity
@Table(name = "field_header")
public class FieldHeader {

	private Long id;
	private Long fieldId;
	private String header;
	private String description;
	private String urlIdentifier;
	private Long languageId;

	public FieldHeader() {
		super();
	}

	public FieldHeader(Long id, Long fieldId, String header, String description, String urlIdentifier,
			Long languageId) {
		super();
		this.id = id;
		this.fieldId = fieldId;
		this.header = header;
		this.description = description;
		this.urlIdentifier = urlIdentifier;
		this.languageId = languageId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "field_id")
	public Long getFieldId() {
		return fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	@Column(name = "header")
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	@Column(name = "description", columnDefinition = "TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "url_identifier")
	public String getUrlIdentifier() {
		return urlIdentifier;
	}

	public void setUrlIdentifier(String urlIdentifier) {
		this.urlIdentifier = urlIdentifier;
	}

	@Column(name = "language_id")
	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

}
