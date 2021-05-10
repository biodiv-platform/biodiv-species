/**
 * 
 */
package com.strandls.species.pojo;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesResourcesPreData {

//	resoruce coming from Draft
	private String path;
	private String url;
	private String type;
	private String caption;
	private Integer rating;
	private Long licenseId;

//	resource coming from observationPull
	private Long observationId;
	private Long resourcesId;

	/**
	 * 
	 */
	public SpeciesResourcesPreData() {
		super();
	}

	/**
	 * @param path
	 * @param url
	 * @param type
	 * @param caption
	 * @param rating
	 * @param licenseId
	 * @param observationId
	 * @param resourcesId
	 */
	public SpeciesResourcesPreData(String path, String url, String type, String caption, Integer rating, Long licenseId,
			Long observationId, Long resourcesId) {
		super();
		this.path = path;
		this.url = url;
		this.type = type;
		this.caption = caption;
		this.rating = rating;
		this.licenseId = licenseId;
		this.observationId = observationId;
		this.resourcesId = resourcesId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Long getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(Long licenseId) {
		this.licenseId = licenseId;
	}

	public Long getObservationId() {
		return observationId;
	}

	public void setObservationId(Long observationId) {
		this.observationId = observationId;
	}

	public Long getResourcesId() {
		return resourcesId;
	}

	public void setResourcesId(Long resourcesId) {
		this.resourcesId = resourcesId;
	}

}
