package com.strandls.species.es.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.esmodule.ApiException;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.taxonomy.controllers.TaxonomyServicesApi;
import com.strandls.taxonomy.pojo.TaxonomicNames;
import com.strandls.userGroup.controller.UserGroupServiceApi;
import com.strandls.userGroup.pojo.UserGroupIbp;

import jakarta.inject.Inject;

public class ESUpdate {

	private final Logger logger = LoggerFactory.getLogger(ESUpdate.class);

	@Inject
	private EsServicesApi esService;

	@Inject
	private UserGroupServiceApi ugService;

	@Inject
	private TaxonomyServicesApi taxonomyService;

	public void esBulkUpload(String speciesIds) {

		if (speciesIds == null || speciesIds.isEmpty()) {
			return;
		}

		List<Map<String, Object>> ESSpeciesShowList = new ArrayList<>();

		try {

			for (String id : speciesIds.split(",")) {
				List<UserGroupIbp> userGroupList = new ArrayList<>();

				try {
					userGroupList = ugService.getSpeciesUserGroup(id);
				} catch (com.strandls.userGroup.ApiException e) {
					logger.error(e.getMessage());
				}

				Map<String, Object> payload = new HashMap<>();
				payload.put("id", id);
				payload.put("userGroups", userGroupList);
				ESSpeciesShowList.add(payload);

			}

			if (!ESSpeciesShowList.isEmpty()) {

				esService.bulkUpdate(SpeciesIndex.INDEX.getValue(), SpeciesIndex.TYPE.getValue(), ESSpeciesShowList);

			}

		} catch (ApiException e) {
			logger.error(e.getMessage());
		}
	}

	public void esBulkTaxonUpload(List<String> speciesIds) {

		if (speciesIds == null || speciesIds.isEmpty()) {
			return;
		}

		List<Map<String, Object>> ESSpeciesShowList = new ArrayList<>();

		try {

			for (String id : speciesIds) {
				TaxonomicNames names = new TaxonomicNames();

				try {
					names = taxonomyService.getNames(id.split("#")[1]);
					System.out.println(names);
				} catch (com.strandls.taxonomy.ApiException e) {
					logger.error(e.getMessage());
				}

				Map<String, Object> payload = new HashMap<>();
				payload.put("id", id.split("#")[0]);
				payload.put("taxonomicNames", names);
				ESSpeciesShowList.add(payload);

			}

			if (!ESSpeciesShowList.isEmpty()) {

				esService.bulkUpdate(SpeciesIndex.INDEX.getValue(), SpeciesIndex.TYPE.getValue(), ESSpeciesShowList);

			}

		} catch (ApiException e) {
			logger.error(e.getMessage());
		}
	}

}
