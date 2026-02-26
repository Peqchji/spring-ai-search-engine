package com.search.ai.ingestion.service;

import com.search.ai.shared.util.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class KafkaConnectorRegistrationService {

    private final RestClient restClient;

    @Value(AppConstants.RESOURCE_ES_SINK_CONNECTOR)
    private Resource elasticsearchSinkResource;

    @Value(AppConstants.RESOURCE_MONGODB_SINK_CONNECTOR)
    private Resource mongodbSinkResource;

    @Value(AppConstants.RESOURCE_MONGODB_SOURCE_CONNECTOR)
    private Resource mongodbSourceResource;

    @Value(AppConstants.PROP_NAME_ES_SINK_CONNECTOR)
    private String esSinkName;

    @Value(AppConstants.PROP_NAME_MONGODB_SOURCE_CONNECTOR)
    private String mongodbSourceName;

    @Value(AppConstants.PROP_KAFKA_CONNECT_URL)
    private String kafkaConnectUrl;

    public KafkaConnectorRegistrationService() {
        this.restClient = RestClient.create();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerConnectors() {
        log.info("Starting automated registration of Kafka Connectors at {}", kafkaConnectUrl);

        registerConnector(esSinkName, elasticsearchSinkResource);
        registerConnector(mongodbSourceName, mongodbSourceResource);
    }

    private void registerConnector(String connectorName, Resource resource) {
        try {
            String configJson = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            // Check if connector already exists
            boolean exists = checkConnectorExists(connectorName);

            if (exists) {
                log.info("Connector '{}' already exists. Updating configuration...", connectorName);

                // For updates, the API expects just the configuration object (the 'config' part
                // of the JSON)
                com.fasterxml.jackson.databind.JsonNode rootNode = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readTree(configJson);
                String configOnly = rootNode.get("config").toString();

                restClient.put()
                        .uri(kafkaConnectUrl + "/connectors/" + connectorName + "/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(configOnly)
                        .retrieve()
                        .toBodilessEntity();

                log.info("Successfully updated Connector: {}", connectorName);
            } else {
                log.info("Registering new Connector: {}", connectorName);
                restClient.post()
                        .uri(kafkaConnectUrl + "/connectors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(configJson)
                        .retrieve()
                        .toBodilessEntity();
                log.info("Successfully registered Connector: {}", connectorName);
            }
        } catch (Exception e) {
            log.error("Failed to register Kafka Connector '{}'. Ensure Kafka Connect is running. Error: {}",
                    connectorName, e.getMessage());
        }
    }

    private boolean checkConnectorExists(String connectorName) {
        try {
            restClient.get()
                    .uri(kafkaConnectUrl + "/connectors/" + connectorName)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientException e) {
            return false;
        }
    }
}
