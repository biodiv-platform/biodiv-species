package com.strandls.species.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Body of POST /v1/species/batch/upload: previewed rows without errors.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpeciesBatchUploadRequest {

	private List<SpeciesBatchUploadRow> rows = new ArrayList<>();

	public SpeciesBatchUploadRequest() {
		super();
	}

	public List<SpeciesBatchUploadRow> getRows() {
		return rows;
	}

	public void setRows(List<SpeciesBatchUploadRow> rows) {
		this.rows = rows;
	}
}
