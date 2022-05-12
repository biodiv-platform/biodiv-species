package com.strandls.species.es.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.strandls.esmodule.ApiException;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.service.SpeciesServices;
import com.strandls.userGroup.controller.UserGroupSerivceApi;
import com.strandls.userGroup.pojo.UserGroupIbp;

public class ESUpdate {

	private final Logger logger = LoggerFactory.getLogger(ESUpdate.class);

	@Inject
	private EsServicesApi esService;

	@Inject
	private SpeciesServices speciesService;

	@Inject
	private UserGroupSerivceApi ugService;

	@Inject
	private ObjectMapper om;

	public void esBulkUpload(String speciesIds) {
		System.out.println("--------------------species es Bulk Upload Started---------" + speciesIds);

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
				if (!userGroupList.isEmpty()) {
					ESSpeciesShowList.add(payload);
				}
			}

			if (!ESSpeciesShowList.isEmpty()) {

				esService.bulkUpdate(SpeciesIndex.INDEX.getValue(), SpeciesIndex.TYPE.getValue(), ESSpeciesShowList);

				System.out.println("--------------completed-------------speciesIds");

			}

		} catch (ApiException e) {
			logger.error(e.getMessage());
		}
	}

}
