package com.strandls.species.es.util;

public class ESBulkUploadThread implements Runnable {

	private ESUpdate esUpdate;
	private String speciesIds;

	/**
	 * 
	 */
	public ESBulkUploadThread() {
		super();
	}

	/**
	 * @param esUpdate
	 * @param speciesIds
	 */
	public ESBulkUploadThread(ESUpdate esUpdate, String speciesIds) {
		super();
		this.esUpdate = esUpdate;
		this.speciesIds = speciesIds;
	}

	@Override
	public void run() {

		esUpdate.esBulkUpload(speciesIds);

	}

}