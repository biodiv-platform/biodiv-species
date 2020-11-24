/**
 * 
 */
package com.strandls.species.pojo;

import java.util.List;

import com.strandls.traits.pojo.TraitsValuePair;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesTrait {

	private String CategoryName;
	private List<TraitsValuePair> traitsValuePairList;

	/**
	 * 
	 */
	public SpeciesTrait() {
		super();
	}

	/**
	 * @param categoryName
	 * @param traitsValuePairList
	 */
	public SpeciesTrait(String categoryName, List<TraitsValuePair> traitsValuePairList) {
		super();
		CategoryName = categoryName;
		this.traitsValuePairList = traitsValuePairList;
	}

	public String getCategoryName() {
		return CategoryName;
	}

	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}

	public List<TraitsValuePair> getTraitsValuePairList() {
		return traitsValuePairList;
	}

	public void setTraitsValuePairList(List<TraitsValuePair> traitsValuePairList) {
		this.traitsValuePairList = traitsValuePairList;
	};

}
