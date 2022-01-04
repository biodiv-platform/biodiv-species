/**
 * 
 */
package com.strandls.species.service;

import com.strandls.esmodule.pojo.MapSearchParams;
import com.strandls.esmodule.pojo.MapSearchQuery;
import com.strandls.species.pojo.MapAggregationResponse;
import com.strandls.species.pojo.SpeciesListPageData;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public interface SpeciesListService {

	public SpeciesListPageData searchList(String index, String type, MapSearchQuery querys, MapAggregationResponse aggregationResult);

	public  MapAggregationResponse mapAggregate(String index, String type,String scientificName, String commonName, String sGroup,
			String userGroupList, String taxonId, String mediaFilter, String traits, String createdOnMaxDate,
			String createdOnMinDate, String revisedOnMinDate, String revisedOnMaxDate,
			String rank,MapSearchParams mapSearchParams);

}
