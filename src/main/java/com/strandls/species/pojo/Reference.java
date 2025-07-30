/**
 * 
 */
package com.strandls.species.pojo;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Abhishek Rudra
 *
 * 
 */

@Entity
@Table(name = "reference")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Reference implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8702718753422424784L;
	private Long id;
	private Long speciesFieldId;
	private String title;
	private String url;
	private Long speciesId;
	private Boolean isDeleted;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "species_field_id")
	public Long getSpeciesFieldId() {
		return speciesFieldId;
	}

	public void setSpeciesFieldId(Long speciesFieldId) {
		this.speciesFieldId = speciesFieldId;
	}

	@Column(name = "title", columnDefinition = "TEXT")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "url", columnDefinition = "TEXT")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "species_id", columnDefinition = "BIGINT")
	public Long getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(Long speciesId) {
		this.speciesId = speciesId;
	}

	@Column(name = "is_deleted")
	@ColumnDefault("false")
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
