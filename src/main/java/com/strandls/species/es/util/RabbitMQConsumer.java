package com.strandls.species.es.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.RecoveryListener;
import com.strandls.esmodule.pojo.TaxonomyUpdateData;
import com.strandls.species.service.Impl.SpeciesServiceImpl;

import jakarta.inject.Inject;

public class RabbitMQConsumer {

	private final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

	private static final String SPECIES_QUEUE = "speciesQueue";

	@Inject
	private Connection connection;

	@Inject
	private SpeciesServiceImpl speciesService;

	// Dedicated to consuming only, never touched by publisher code, so it is
	// safe for basicConsume's own dispatch thread(s) to own exclusively.
	private Channel consumerChannel;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private synchronized Channel getConsumerChannel() throws Exception {
		if (consumerChannel == null || !consumerChannel.isOpen()) {
			consumerChannel = connection.createChannel();
		}
		return consumerChannel;
	}

	/**
	 * Subscribes the consumer and, since topology recovery is disabled on the
	 * shared {@link Connection} (see {@link com.strandls.species.RabbitMqConnection}),
	 * re-subscribes it itself whenever the connection recovers from a drop -
	 * the broker forgets consumer registrations on disconnect, so this is a
	 * plain re-subscribe rather than a duplicate.
	 */
	public void startConsuming() throws Exception {
		listenToTaxonomyEvents();
		if (connection instanceof Recoverable) {
			((Recoverable) connection).addRecoveryListener(new RecoveryListener() {
				@Override
				public void handleRecovery(Recoverable recoverable) {
					try {
						listenToTaxonomyEvents();
						logger.info("Re-subscribed RabbitMQ consumer after connection recovery");
					} catch (Exception e) {
						logger.error("Failed to re-subscribe RabbitMQ consumer after recovery", e);
					}
				}

				@Override
				public void handleRecoveryStarted(Recoverable recoverable) {
				}
			});
		}
	}

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

		getConsumerChannel().basicConsume(SPECIES_QUEUE, true, deliverCallback, consumerTag -> {
		});
	}
}
