/**
 * 
 */
package com.strandls.species.pojo;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesListTiles {

	private Long id;
	private String name;
	private String context;
	private String reprImage;
	private String status;
	private String commonName;
	private Long sGroup;

	/**
	 * 
	 */
	public SpeciesListTiles() {
		super();
	}

	/**
	 * @param id
	 * @param name
	 * @param context
	 * @param reprImage
	 * @param status
	 */
	public SpeciesListTiles(Long id, String name, String context, String reprImage, String status, String commonName,
			Long sGroup) {
		super();
		this.id = id;
		this.name = name;
		this.context = context;
		this.reprImage = reprImage;
		this.status = status;
		this.commonName = commonName;
		this.sGroup = sGroup;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getReprImage() {
		return reprImage;
	}

	public void setReprImage(String reprImage) {
		this.reprImage = reprImage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public Long getsGroup() {
		return sGroup;
	}

	public void setsGroup(Long sGroup) {
		this.sGroup = sGroup;
	}

}
