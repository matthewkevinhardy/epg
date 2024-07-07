package epg.repos;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import epg.documents.ProgrammeDoc;

public interface ProgrammeRepo extends ElasticsearchRepository<ProgrammeDoc, String> {

	@Query(" { \"range\": { \"start\": { \"gte\": \"now-1d/d\", \"lte\": \"now/d\" } } } ")
	public Page<ProgrammeDoc> findByLast24Hours(Pageable pageable);

	@Query(" { \"range\": { \"start\": { \"gte\": \"?0\", \"lte\": \"?1\" } } } ")
	public Page<ProgrammeDoc> findByStart(LocalDateTime begin, LocalDateTime end, Pageable pageable);

	// { "query" : { "bool" : { "must" : [ { "query_string" : { "query" : "?0",
	// "fields" : [ "channel" ] } }, { "query_string" : { "range": { "start": {
	// "gte": "?1", "lte": "?2" } } } } ] } }}

	// @Query(" { \"bool\" : { \"must\" : [ { \"query_string\" : { \"query\" :
	// \"?0\", \"fields\" : [ \"channel\" ] } }, { \"query_string\" : { \"range\": {
	// \"start\": { \"gte\": \"?1\", \"lte\": \"?2\" } } } } ] } }")
	public Page<ProgrammeDoc> findByChannelAndStartBetweenOrStopBetween(String channel, LocalDateTime fromStart,
			LocalDateTime toStart, LocalDateTime fromStop, LocalDateTime toStop, Pageable pageable);

	public Optional<ProgrammeDoc> findByChannelAndStartLessThanAndStopGreaterThan(String channel, LocalDateTime toStart,
			LocalDateTime fromStop);

	public Page<ProgrammeDoc> findByChannelAndStopGreaterThanAndStopLessThan(String channel, LocalDateTime toStart,
			LocalDateTime fromStop, Pageable pageable);

	public Page<ProgrammeDoc> findByChannel(String channel, Pageable pageable);

	public Page<ProgrammeDoc> findByDescription(String description, Pageable pageable);

	public Page<ProgrammeDoc> findByDescriptionAndDescriptionLang(String description, String descriptionLang,
			Pageable pageable);
}