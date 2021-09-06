/**
 * 
 */
package com.strandls.species.service;

import com.strandls.species.pojo.SpeciesListPageData;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public interface SpeciesListService {

	public SpeciesListPageData searchList(String orderBy, String offset);

}
