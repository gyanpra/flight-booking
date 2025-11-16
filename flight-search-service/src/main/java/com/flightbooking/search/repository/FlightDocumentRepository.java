package com.flightbooking.search.repository;

import com.flightbooking.search.model.FlightDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightDocumentRepository extends ElasticsearchRepository<FlightDocument, String> {
}
