/**
 * 
 */
package com.strandls.species.pojo;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Abhishek Rudra
 *
 * 
 */

@Entity
@Table(name = "species_field")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpeciesField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3085147736923167548L;
	private Long id;
	private String description;
	private Long fieldId;
	private Long speciesId;
	private String status;
	private String classes;
	private Date dateCreated;
	private Date lastUpdated;
	private Date uploadedTime;
	private Long uploaderId;
	private Long languageId;
	private Long dataTableId;
	private Boolean isDeleted;

	/**
	 * 
	 */
	public SpeciesField() {
		super();
	}

	/**
	 * @param id
	 * @param description
	 * @param fieldId
	 * @param speciesId
	 * @param status
	 * @param classes
	 * @param dateCreated
	 * @param lastUpdated
	 * @param uploadedTime
	 * @param uploaderId
	 * @param languageId
	 * @param dataTableId
	 * @param isDeleted
	 */
	public SpeciesField(Long id, String description, Long fieldId, Long speciesId, String status, String classes,
			Date dateCreated, Date lastUpdated, Date uploadedTime, Long uploaderId, Long languageId, Long dataTableId,
			Boolean isDeleted) {
		super();
		this.id = id;
		this.description = description;
		this.fieldId = fieldId;
		this.speciesId = speciesId;
		this.status = status;
		this.classes = classes;
		this.dateCreated = dateCreated;
		this.lastUpdated = lastUpdated;
		this.uploadedTime = uploadedTime;
		this.uploaderId = uploaderId;
		this.languageId = languageId;
		this.dataTableId = dataTableId;
		this.isDeleted = isDeleted;
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

	@Column(name = "description", columnDefinition = "TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "field_id")
	public Long getFieldId() {
		return fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	@Column(name = "species_id")
	public Long getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(Long speciesId) {
		this.speciesId = speciesId;
	}

	@Column(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "class")
	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	@Column(name = "date_created")
	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Column(name = "last_updated")
	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	@Column(name = "upload_time")
	public Date getUploadedTime() {
		return uploadedTime;
	}

	public void setUploadedTime(Date uploadedTime) {
		this.uploadedTime = uploadedTime;
	}

	@Column(name = "uploader_id")
	public Long getUploaderId() {
		return uploaderId;
	}

	public void setUploaderId(Long uploaderId) {
		this.uploaderId = uploaderId;
	}

	@Column(name = "language_id")
	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	@Column(name = "data_table_id")
	public Long getDataTableId() {
		return dataTableId;
	}

	public void setDataTableId(Long dataTableId) {
		this.dataTableId = dataTableId;
	}

	@Column(name = "is_deleted", columnDefinition = "boolean default false")
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
