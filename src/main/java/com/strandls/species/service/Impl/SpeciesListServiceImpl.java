/**
 * 
 */
package com.strandls.species.service.Impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.resource.controllers.ResourceServicesApi;
import com.strandls.resource.pojo.Resource;
import com.strandls.species.dao.SpeciesDao;
import com.strandls.species.pojo.Species;
import com.strandls.species.pojo.SpeciesListPageData;
import com.strandls.species.pojo.SpeciesListTiles;
import com.strandls.species.service.SpeciesListService;
import com.strandls.taxonomy.controllers.TaxonomyServicesApi;
import com.strandls.taxonomy.pojo.TaxonomyDefinition;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesListServiceImpl implements SpeciesListService {

	private final Logger logger = LoggerFactory.getLogger(SpeciesListServiceImpl.class);

//	Dao injection
	@Inject
	private SpeciesDao speciesDao;

//	service Injection

	@Inject
	private ResourceServicesApi resourcesService;

	@Inject
	private TaxonomyServicesApi taxonomyService;

	@Override
	public SpeciesListPageData searchList(String orderBy, String offset) {
		List<Species> speciesList = speciesDao.fetchInBatches(orderBy, offset);
		Long totalCount = speciesDao.fetchCountOfSpeices();
		List<SpeciesListTiles> tileData = new ArrayList<SpeciesListTiles>();
		Resource resource = null;
		try {
			for (Species species : speciesList) {

				if (species.getReprImageId() != null)
					resource = resourcesService.getResourceDataById(species.getReprImageId().toString());

				TaxonomyDefinition taxonomyDefinition = taxonomyService
						.getTaxonomyConceptName(species.getTaxonConceptId().toString());
				tileData.add(new SpeciesListTiles(species.getId(), taxonomyDefinition.getItalicisedForm(),
						resource != null ? resource.getFileName() : null, taxonomyDefinition.getStatus()));
			}

			SpeciesListPageData result = new SpeciesListPageData(totalCount, tileData);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

}
