package com.strandls.species.service;

import java.util.List;

import com.strandls.species.pojo.SpeciesBatchPreviewRequest;
import com.strandls.species.pojo.SpeciesBatchPreviewResult;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Species page batch upload (biodiv-ui species/batch-upload).
 */
public interface SpeciesBatchService {

	/** Largest number of rows a single preview call accepts */
	int MAX_PREVIEW_ROWS = 2000;

	/**
	 * Validates and resolves the mapped spreadsheet rows without writing anything:
	 * contributors to users, license and language to ids, and the species field
	 * create payloads.
	 *
	 * @throws SecurityException        if the caller is not an admin
	 * @throws IllegalArgumentException if the request is empty or too large
	 */
	List<SpeciesBatchPreviewResult> preview(HttpServletRequest request, SpeciesBatchPreviewRequest previewRequest);
}
