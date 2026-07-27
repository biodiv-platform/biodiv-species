package com.strandls.species.es.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.strandls.esmodule.pojo.TaxonomyUpdateData;
import com.strandls.species.service.Impl.SpeciesServiceImpl;

import jakarta.inject.Inject;

public class RabbitMQConsumer {

	private static final String SPECIES_QUEUE = "speciesQueue";

	@Inject
	private Channel channel;

	@Inject
	private SpeciesServiceImpl speciesService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public void listenToTaxonomyEvents() throws Exception {
		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), "UTF-8");
			System.out.println("----[SPECIES TAXONOMY EVENT]----");

			try {
				System.out.println("Received taxonomy event for species: " + message);
				TaxonomyUpdateData event = objectMapper.readValue(message, TaxonomyUpdateData.class);
				speciesService.handleTaxonomyUpdate(event);

			} catch (Exception e) {
				System.err.println("Failed to process event: " + e.getMessage());
			}
		};

		channel.basicConsume(SPECIES_QUEUE, true, deliverCallback, consumerTag -> {
		});
	}
}