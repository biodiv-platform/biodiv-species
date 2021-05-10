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
	private String reprImage;
	private String status;

	/**
	 * 
	 */
	public SpeciesListTiles() {
		super();
	}

	/**
	 * @param id
	 * @param name
	 * @param reprImage
	 * @param status
	 */
	public SpeciesListTiles(Long id, String name, String reprImage, String status) {
		super();
		this.id = id;
		this.name = name;
		this.reprImage = reprImage;
		this.status = status;
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

}
