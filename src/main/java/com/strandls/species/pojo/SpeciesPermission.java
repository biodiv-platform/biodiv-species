/**
 * 
 */
package com.strandls.species.pojo;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesPermission {

	private Boolean isContributor;
	private Boolean isFollower;

	/**
	 * 
	 */
	public SpeciesPermission() {
		super();
	}

	/**
	 * @param isContributor
	 * @param isFollower
	 */
	public SpeciesPermission(Boolean isContributor, Boolean isFollower) {
		super();
		this.isContributor = isContributor;
		this.isFollower = isFollower;
	}

	public Boolean getIsContributor() {
		return isContributor;
	}

	public void setIsContributor(Boolean isContributor) {
		this.isContributor = isContributor;
	}

	public Boolean getIsFollower() {
		return isFollower;
	}

	public void setIsFollower(Boolean isFollower) {
		this.isFollower = isFollower;
	}

}
