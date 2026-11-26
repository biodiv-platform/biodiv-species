package com.strandls.species.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Body of POST /v1/species/batch/preview.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpeciesBatchPreviewRequest {

	private List<SpeciesBatchPreviewRow> rows = new ArrayList<>();

	public SpeciesBatchPreviewRequest() {
		super();
	}

	public List<SpeciesBatchPreviewRow> getRows() {
		return rows;
	}

	public void setRows(List<SpeciesBatchPreviewRow> rows) {
		this.rows = rows;
	}
}
