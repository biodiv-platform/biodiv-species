package com.strandls.species.pojo;

public class ReferenceCreateData {

	private Long speciesId;
	private String title;
	private String url;

	public ReferenceCreateData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ReferenceCreateData(Long speciesId, String title, String url) {
		super();
		this.speciesId = speciesId;
		this.title = title;
		this.url = url;
	}

	public Long getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(Long speciesId) {
		this.speciesId = speciesId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
