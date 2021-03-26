/**
 * 
 */
package com.strandls.species.pojo;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesPullData {

	private Long observationId;
	private Long resourceId;

	/**
	 * 
	 */
	public SpeciesPullData() {
		super();
	}

	/**
	 * @param observationId
	 * @param resourceId
	 */
	public SpeciesPullData(Long observationId, Long resourceId) {
		super();
		this.observationId = observationId;
		this.resourceId = resourceId;
	}

	public Long getObservationId() {
		return observationId;
	}

	public void setObservationId(Long observationId) {
		this.observationId = observationId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

}
