/**
 * 
 */
package com.strandls.species.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Abhishek Rudra
 *
 * 
 */

@Entity
@Table(name = "field")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Field implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4705764533597536423L;
	private Long id;
	private String category;
	private String concept;
	private String description;
	private Integer displayOrder;
	private String subCategory;
	private String urlIdentifier;
	private Long connection;
	private Long languageId;

	public Field() {
		super();
	}

	public Field(Long id, String category, String concept, String description, Integer displayOrder, String subCategory,
			String urlIdentifier, Long connection, Long languageId) {
		super();
		this.id = id;
		this.category = category;
		this.concept = concept;
		this.description = description;
		this.displayOrder = displayOrder;
		this.subCategory = subCategory;
		this.urlIdentifier = urlIdentifier;
		this.connection = connection;
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

	@Column(name = "category")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Column(name = "concept")
	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	@Column(name = "description", columnDefinition = "TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "display_order")
	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Column(name = "sub_category")
	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	@Column(name = "url_identifier")
	public String getUrlIdentifier() {
		return urlIdentifier;
	}

	public void setUrlIdentifier(String urlIdentifier) {
		this.urlIdentifier = urlIdentifier;
	}

	@Column(name = "connection")
	public Long getConnection() {
		return connection;
	}

	public void setConnection(Long connection) {
		this.connection = connection;
	}

	@Column(name = "language_id")
	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

}
