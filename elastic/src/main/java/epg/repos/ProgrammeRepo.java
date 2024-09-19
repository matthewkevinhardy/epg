package epg.repos;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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

	@Query("{\"bool\":{\"must\":[{\"query_string\":{\"default_operator\":\"and\",\"fields\":[\"channel\"],\"query\":\"?0\"}},{\"bool\":{\"should\":[{\"range\":{\"start\":{\"gte\":\"?1\",\"lte\":\"?2\"}}},{\"range\":{\"stop\":{\"gte\":\"?1\",\"lte\":\"?2\"}}},  {\"bool\": {\"must\": [{ \"range\": { \"start\": { \"lt\": \"now\" } } },{ \"range\": { \"stop\": { \"gt\": \"now\" } } }]}}  ]}}]}}")
	public Page<ProgrammeDoc> findInTimeSlot(String channel, ZonedDateTime begin, ZonedDateTime end, Pageable pageable);

	@Query("{\"bool\":{\"must\":[{\"query_string\":{\"default_operator\":\"and\",\"fields\":[\"channel\"],\"query\":\"?0\"}},{\"range\":{\"start\":{\"lte\":\"now\"}}},{\"range\":{\"stop\":{\"gte\":\"now\"}}}]}}")
	public ProgrammeDoc findNow(String channel);

	public ProgrammeDoc findByChannelAndStart(String channel, ZonedDateTime start);

	public Page<ProgrammeDoc> findByChannelAndStartBetweenOrStopBetween(String channel, ZonedDateTime fromStart,
			ZonedDateTime toStart, ZonedDateTime fromStop, ZonedDateTime toStop, Pageable pageable);

	public Optional<ProgrammeDoc> findByChannelAndStartLessThanAndStopGreaterThan(String channel, ZonedDateTime toStart,
			ZonedDateTime fromStop);

	public Page<ProgrammeDoc> findByChannelAndStopGreaterThanAndStopLessThan(String channel, ZonedDateTime toStart,
			ZonedDateTime fromStop, Pageable pageable);

	public Page<ProgrammeDoc> findByChannel(String channel, Pageable pageable);

	public Page<ProgrammeDoc> findByDescription(String description, Pageable pageable);

	public Page<ProgrammeDoc> findByDescriptionAndDescriptionLang(String description, String descriptionLang,
			Pageable pageable);

	public long deleteByStopLessThan(ZonedDateTime stop);
}