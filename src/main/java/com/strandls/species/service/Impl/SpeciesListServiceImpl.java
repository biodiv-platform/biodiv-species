/**
 * 
 */
package com.strandls.species.service.Impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.AggregationResponse;
import com.strandls.esmodule.pojo.MapDocument;
import com.strandls.esmodule.pojo.MapResponse;
import com.strandls.esmodule.pojo.MapSearchParams;
import com.strandls.esmodule.pojo.MapSearchQuery;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.species.es.util.ESUtility;
import com.strandls.species.es.util.SpeciesIndex;
import com.strandls.species.pojo.MapAggregationResponse;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.pojo.SpeciesListPageData;
import com.strandls.species.pojo.SpeciesListTiles;
import com.strandls.species.service.SpeciesListService;
import com.strandls.taxonomy.pojo.CommonName;
import com.strandls.taxonomy.pojo.TaxonomicNames;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesListServiceImpl implements SpeciesListService {

	private final Logger logger = LoggerFactory.getLogger(SpeciesListServiceImpl.class);

//	Dao injection

	@Inject
	private EsServicesApi esService;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private ESUtility esUtility;

	@Override
	public SpeciesListPageData searchList(String index, String type, MapSearchQuery querys,
			MapAggregationResponse aggregationResult) {
		SpeciesListPageData listData = null;

		try {
			MapResponse result = esService.search(index, type, null, null, false, null, null, querys);
			List<MapDocument> documents = result.getDocuments();
			Long totalCount = result.getTotalDocuments();
			List<ShowSpeciesPage> specieList = new ArrayList<ShowSpeciesPage>();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			objectMapper.setDateFormat(df);
			for (MapDocument document : documents) {
				JsonNode rootNode = objectMapper.readTree(document.getDocument().toString());
				((ObjectNode) rootNode).remove("id");
				((ObjectNode) rootNode).replace("featured", null);
				((ObjectNode) rootNode).replace("facts", null);
				JsonNode child = ((ObjectNode) rootNode).get("taxonomyDefinition");
				((ObjectNode) child).replace("defaultHierarchy", null);

				try {

					specieList.add(objectMapper.readValue(String.valueOf(rootNode), ShowSpeciesPage.class));
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}

			List<SpeciesListTiles> speciesListTile = specieList.stream()
					.map(item -> new SpeciesListTiles(item.getSpecies().getId(), item.getSpecies().getTitle(),
							item.getSpecies().getReprImageId() != null ? getResourceImageAndContext(item)[1] : null,
							item.getSpecies().getReprImageId() != null ? getResourceImageAndContext(item)[0] : null,
							item.getTaxonomyDefinition().getStatus(),
							item.getTaxonomicNames() != null ? getPrefferedCommonName(item.getTaxonomicNames()) : null,
							item.getSpeciesGroup() != null ? item.getSpeciesGroup().getId() : null))
					.collect(Collectors.toList());

			listData = new SpeciesListPageData(totalCount, speciesListTile, aggregationResult);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return listData;
	}

	private String getPrefferedCommonName(TaxonomicNames taxonomicName) {
		String preferredCommonName = null;
		if (taxonomicName.getCommonNames() == null || taxonomicName.getCommonNames().get(0) == null
				|| taxonomicName.getCommonNames().isEmpty())
			return preferredCommonName;
		List<CommonName> prefName = taxonomicName.getCommonNames().stream()
				.filter(item -> item.getIsPreffered() != null && item.getIsPreffered().equals(Boolean.TRUE))
				.collect(Collectors.toList());
		if (!prefName.isEmpty()) {
			preferredCommonName = prefName.get(0).getName();
		}

		return preferredCommonName;
	}

	private String[] getResourceImageAndContext(ShowSpeciesPage showSpecies) {
		String[] result = new String[2];

		List<ResourceData> resource = showSpecies.getResourceData().stream()
				.filter(resc -> resc.getResource().getId().toString()
						.contentEquals(showSpecies.getSpecies().getReprImageId().toString()))
				.collect(Collectors.toList());
		if (resource != null && !resource.isEmpty()) {
			result[0] = resource.get(0).getResource().getFileName();
			result[1] = resource.get(0).getResource().getContext();
		}

		return result;

	}

	private void getAggregateLatch(String index, String type, String filter, MapSearchQuery searchQuery,
			Map<String, AggregationResponse> mapResponse, CountDownLatch latch, String namedAgg) {

		LatchThreadWorker worker = new LatchThreadWorker(index, type, filter, searchQuery, mapResponse, namedAgg, latch,
				esService);
		worker.start();

	}

	@Override
	public MapAggregationResponse mapAggregate(String index, String type, String scientificName, String commonName,
			String sGroup, String userGroupList, String taxonId, String mediaFilter, String traits,
			String createdOnMaxDate, String createdOnMinDate, String revisedOnMinDate, String revisedOnMaxDate,
			String rank, MapSearchParams mapSearchParams) {

		MapSearchQuery mapSearchQueryFilter;
		MapSearchQuery mapSearchQuery = esUtility.getMapSearchQuery(scientificName, commonName, sGroup, userGroupList,
				taxonId, mediaFilter, traits, createdOnMaxDate, createdOnMinDate, revisedOnMinDate, revisedOnMaxDate,
				rank, mapSearchParams);

		String omiter = null;
		MapAggregationResponse aggregationResponse = new MapAggregationResponse();

		Map<String, AggregationResponse> mapAggResponse = new HashMap<String, AggregationResponse>();

		int totalLatch = 5;
//		latch count down
		CountDownLatch latch = new CountDownLatch(totalLatch);

//		sGroup aggregation
		if (sGroup != null && !sGroup.isEmpty()) {

			mapSearchQueryFilter = esUtility.getMapSearchQuery(scientificName, commonName, omiter, userGroupList,
					taxonId, mediaFilter, traits, createdOnMaxDate, createdOnMinDate, revisedOnMinDate,
					revisedOnMaxDate, rank, mapSearchParams);

			getAggregateLatch(index, type, SpeciesIndex.SGROUP.getValue(), mapSearchQueryFilter, mapAggResponse, latch,
					null);

		} else {
			getAggregateLatch(index, type, SpeciesIndex.SGROUP.getValue(), mapSearchQuery, mapAggResponse, latch, null);
		}

//		userGroupList aggregation

		if (userGroupList != null && !userGroupList.isEmpty()) {

			mapSearchQueryFilter = esUtility.getMapSearchQuery(scientificName, commonName, sGroup, omiter, taxonId,
					mediaFilter, traits, createdOnMaxDate, createdOnMinDate, revisedOnMinDate, revisedOnMaxDate, rank,
					mapSearchParams);

			getAggregateLatch(index, type, SpeciesIndex.USERGROUPID.getValue(), mapSearchQueryFilter, mapAggResponse,
					latch, null);

		} else {
			getAggregateLatch(index, type, SpeciesIndex.USERGROUPID.getValue(), mapSearchQuery, mapAggResponse, latch,
					null);
		}

//		mediaFilter aggregation

		if (mediaFilter != null && !mediaFilter.isEmpty()) {

			mapSearchQueryFilter = esUtility.getMapSearchQuery(scientificName, commonName, sGroup, userGroupList,
					taxonId, omiter, traits, createdOnMaxDate, createdOnMinDate, revisedOnMinDate, revisedOnMaxDate,
					rank, mapSearchParams);

			getAggregateLatch(index, type, SpeciesIndex.MEDIA_TYPE_KEYWORD.getValue(), mapSearchQueryFilter,
					mapAggResponse, latch, null);

		} else {
			getAggregateLatch(index, type, SpeciesIndex.MEDIA_TYPE_KEYWORD.getValue(), mapSearchQuery, mapAggResponse,
					latch, null);
		}

//		traits aggregation
		if (traits != null && !traits.isEmpty()) {

			mapSearchQueryFilter = esUtility.getMapSearchQuery(scientificName, commonName, sGroup, userGroupList,
					taxonId, mediaFilter, omiter, createdOnMaxDate, createdOnMinDate, revisedOnMinDate,
					revisedOnMaxDate, rank, mapSearchParams);

			getAggregateLatch(index, type, SpeciesIndex.FACT_KEYWORD.getValue(), mapSearchQueryFilter, mapAggResponse,
					latch, null);

		} else {
			getAggregateLatch(index, type, SpeciesIndex.FACT_KEYWORD.getValue(), mapSearchQuery, mapAggResponse, latch,
					null);
		}

//		rank aggregation
		if (rank != null && !rank.isEmpty()) {

			mapSearchQueryFilter = esUtility.getMapSearchQuery(scientificName, commonName, sGroup, userGroupList,
					taxonId, mediaFilter, traits, createdOnMaxDate, createdOnMinDate, revisedOnMinDate,
					revisedOnMaxDate, omiter, mapSearchParams);

			getAggregateLatch(index, type, SpeciesIndex.RANK_KEYWORD.getValue(), mapSearchQueryFilter, mapAggResponse,
					latch, null);

		} else {
			getAggregateLatch(index, type, SpeciesIndex.RANK_KEYWORD.getValue(), mapSearchQuery, mapAggResponse, latch,
					null);
		}
		try {
			latch.await();
		} catch (Exception e) {
			logger.error(e.getMessage());
			Thread.currentThread().interrupt();
		}

		aggregationResponse
				.setGroupSpeciesName(mapAggResponse.get(SpeciesIndex.SGROUP.getValue()).getGroupAggregation());
		aggregationResponse
				.setGroupUserGroupName(mapAggResponse.get(SpeciesIndex.USERGROUPID.getValue()).getGroupAggregation());
		aggregationResponse
				.setGroupTraits(mapAggResponse.get(SpeciesIndex.FACT_KEYWORD.getValue()).getGroupAggregation());
		aggregationResponse.setGroupMediaType(
				mapAggResponse.get(SpeciesIndex.MEDIA_TYPE_KEYWORD.getValue()).getGroupAggregation());

		aggregationResponse
				.setGroupRank(mapAggResponse.get(SpeciesIndex.RANK_KEYWORD.getValue()).getGroupAggregation());
		return aggregationResponse;
	}

}
