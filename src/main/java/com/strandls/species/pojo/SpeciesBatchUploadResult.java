package com.strandls.species.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Outcome of one sheet row in POST /v1/species/batch/upload.
 */
public class SpeciesBatchUploadResult {

	public static final String SAVED = "SAVED"; // every species field of the row was added
	public static final String SKIPPED = "SKIPPED"; // the row has errors or nothing to add; nothing saved
	public static final String FAILED = "FAILED"; // saving failed; nothing of the row was saved

	private Integer rowNumber;
	private String scientificName;
	private Long speciesId;
	private String status;
	private String message;
	private List<Long> speciesFieldIds = new ArrayList<>();

	public SpeciesBatchUploadResult() {
		super();
	}

	public SpeciesBatchUploadResult(Integer rowNumber, String scientificName, Long speciesId, String status,
			String message) {
		super();
		this.rowNumber = rowNumber;
		this.scientificName = scientificName;
		this.speciesId = speciesId;
		this.status = status;
		this.message = message;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Long> getSpeciesFieldIds() {
		return speciesFieldIds;
	}

	public void setSpeciesFieldIds(List<Long> speciesFieldIds) {
		this.speciesFieldIds = speciesFieldIds;
	}
}
