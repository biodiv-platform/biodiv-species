package com.strandls.species.service.Impl;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.AggregationResponse;
import com.strandls.esmodule.pojo.MapSearchQuery;

/**
 * 
 * @author vishnu
 *
 */
public class LatchThreadWorker extends Thread {

	private final Logger logger = LoggerFactory.getLogger(LatchThreadWorker.class);

	private String index;
	private String type;
	private String filter;
	private MapSearchQuery searchQuery;
	private Map<String, AggregationResponse> mapResponse;
	private String namedAgg;
	private CountDownLatch latch;

	@Inject
	private EsServicesApi esService;

	/**
	 * @param index
	 * @param type
	 * @param filter
	 * @param geoAggregationField
	 * @param searchQuery
	 * @param mapResponse
	 * @param namedAgg
	 * @param latch
	 * @param esService
	 */
	public LatchThreadWorker(String index, String type, String filter, MapSearchQuery searchQuery,
			Map<String, AggregationResponse> mapResponse, String namedAgg, CountDownLatch latch,
			EsServicesApi esService) {
		super();
		this.index = index;
		this.type = type;
		this.filter = filter;
		this.searchQuery = searchQuery;
		this.mapResponse = mapResponse;
		this.namedAgg = namedAgg;
		this.latch = latch;
		this.esService = esService;

	}

	@Override
	public void run() {
		try {
			AggregationResponse response = esService.getAggregation(index, type, filter, null, null, searchQuery);
			if (namedAgg != null && !namedAgg.isEmpty())
				mapResponse.put(namedAgg, response);
			else
				mapResponse.put(filter, response);

			// latch.countDown();
		} catch (Exception e) {
			// logger.error(e.getMessage());
			logger.error("Aggregation failed for filter {}: {}", filter, e.getMessage());
		} finally {
			latch.countDown();
		}
	}

}
