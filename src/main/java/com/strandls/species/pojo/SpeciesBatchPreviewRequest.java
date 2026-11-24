package com.strandls.species.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Body of POST /v1/species/batch/preview.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpeciesBatchPreviewRequest {

	/** License used for rows whose License cell is blank (the UI sends its site default) */
	private Long defaultLicenseId;
	private List<SpeciesBatchPreviewRow> rows = new ArrayList<>();

	public SpeciesBatchPreviewRequest() {
		super();
	}

	public Long getDefaultLicenseId() {
		return defaultLicenseId;
	}

	public void setDefaultLicenseId(Long defaultLicenseId) {
		this.defaultLicenseId = defaultLicenseId;
	}

	public List<SpeciesBatchPreviewRow> getRows() {
		return rows;
	}

	public void setRows(List<SpeciesBatchPreviewRow> rows) {
		this.rows = rows;
	}
}
