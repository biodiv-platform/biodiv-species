package com.strandls.species.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * One spreadsheet row of the species batch upload, as mapped by the UI. Everything
 * except the species field values is the raw cell text: the preview parses and
 * validates it.
 *
 * <ul>
 * <li>contributor: one or more user emails separated by , ; or new line</li>
 * <li>license: license name or id (blank = request default)</li>
 * <li>speciesId: id of the species page to fill (required, used as given)</li>
 * <li>language: language name (blank = default)</li>
 * <li>references: one per line or ; separated, each "Title | URL", "Title" or
 * "URL"</li>
 * <li>media: http(s) image / audio / video URLs separated by , ; or new line</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpeciesBatchPreviewRow {

	private Integer rowNumber;
	private String scientificName;
	private String speciesId;
	private String contributor;
	private String attribution;
	private String license;
	private String language;
	private String status;
	private String references;
	private String media;
	private List<SpeciesBatchFieldValue> fields = new ArrayList<>();

	public SpeciesBatchPreviewRow() {
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

	public String getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(String speciesId) {
		this.speciesId = speciesId;
	}

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	public String getAttribution() {
		return attribution;
	}

	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReferences() {
		return references;
	}

	public void setReferences(String references) {
		this.references = references;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public List<SpeciesBatchFieldValue> getFields() {
		return fields;
	}

	public void setFields(List<SpeciesBatchFieldValue> fields) {
		this.fields = fields;
	}
}
