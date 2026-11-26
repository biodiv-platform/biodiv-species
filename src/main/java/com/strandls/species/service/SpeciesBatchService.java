package com.strandls.species.service;

import java.util.List;

import com.strandls.species.pojo.SpeciesBatchPreviewRequest;
import com.strandls.species.pojo.SpeciesBatchPreviewResult;
import com.strandls.species.pojo.SpeciesBatchUploadRequest;
import com.strandls.species.pojo.SpeciesBatchUploadResult;

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

	/**
	 * Saves previewed rows: the field payloads {@link #preview} returned, sent back as
	 * they are. No preview lookup is repeated; only quick checks run (admin, required
	 * values present, the species page exists). Each row is saved in one transaction
	 * (all of its species fields or none), activity is logged once per row, and each
	 * species page is reindexed once.
	 *
	 * @throws SecurityException        if the caller is not an admin
	 * @throws IllegalArgumentException if the request is empty or too large
	 */
	List<SpeciesBatchUploadResult> upload(HttpServletRequest request, SpeciesBatchUploadRequest uploadRequest);
}
