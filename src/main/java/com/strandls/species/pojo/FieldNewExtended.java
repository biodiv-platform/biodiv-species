package com.strandls.species.pojo;

public class FieldNewExtended extends FieldNew {
	private String description;
	private String urlIdentifier;

	public FieldNewExtended() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FieldNewExtended(Long id, Long parentId, Long displayOrder, String label, String header, String path) {
		super(id, parentId, displayOrder, label, header, path);
		// TODO Auto-generated constructor stub
	}

	public FieldNewExtended(String description, String urlIdentifier) {
		super();
		this.description = description;
		this.urlIdentifier = urlIdentifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrlIdentifier() {
		return urlIdentifier;
	}

	public void setUrlIdentifier(String urlIdentifier) {
		this.urlIdentifier = urlIdentifier;
	}

}
