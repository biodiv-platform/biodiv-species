package com.strandls.species.es.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.strandls.esmodule.ApiException;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.service.SpeciesServices;

public class ESUpdate {

	private final Logger logger = LoggerFactory.getLogger(ESUpdate.class);

	@Inject
	private EsServicesApi esService;

	@Inject
	private SpeciesServices speciesService;

	@Inject
	private ObjectMapper om;

	public void esBulkUpload(String speciesIds) {
		System.out.println("--------------------species es Bulk Upload Started---------" + speciesIds);

		if (speciesIds == null || speciesIds.isEmpty()) {
			return;
		}

		List<ShowSpeciesPage> ESSpeciesShowList = new ArrayList<ShowSpeciesPage>();

		try {

			for (String id : speciesIds.split(",")) {
				ESSpeciesShowList.add(speciesService.showSpeciesPage(Long.parseLong(id)));
			}

			if (!ESSpeciesShowList.isEmpty()) {

				List<Map<String, Object>> bulkEsDoc = ESSpeciesShowList.stream().map(s -> {
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
					om.setDateFormat(df);
					@SuppressWarnings("unchecked")
					String payload = null;
					JsonNode rootNode = null;
					
					try {
						payload = om.writeValueAsString(s);
						rootNode = om.readTree(payload);
					} catch (JsonProcessingException e) {
						logger.error(e.getMessage());
					}

					if (s.getTaxonomyDefinition().getDefaultHierarchy() != null
							&& !s.getTaxonomyDefinition().getDefaultHierarchy().isEmpty() && rootNode != null) {
						JsonNode child = ((ObjectNode) rootNode).get("taxonomyDefinition");
						((ObjectNode) child).replace("defaultHierarchy", null);
					}
					
					return om.convertValue(rootNode, Map.class);

				}).collect(Collectors.toList());

				String json = om.writeValueAsString(bulkEsDoc);

				esService.bulkUpload(SpeciesIndex.INDEX.getValue(), SpeciesIndex.TYPE.getValue(), json.toString());

				System.out.println("--------------completed-------------speciesIds");

			}

		} catch (ApiException | JsonProcessingException e) {
			logger.error(e.getMessage());
		}
	}

}
