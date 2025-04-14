package epg.repos;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import epg.documents.ProgrammeDoc;

public interface ProgrammeRepo extends ElasticsearchRepository<ProgrammeDoc, String> {

	@Query("{\"bool\":{\"must\":[{\"query_string\":{\"default_operator\":\"and\",\"fields\":[\"channel\"],\"query\":\"?0\"}},{\"bool\":{\"should\":[{\"range\":{\"start\":{\"gte\":\"?1\",\"lte\":\"?2\"}}},{\"range\":{\"stop\":{\"gte\":\"?1\",\"lte\":\"?2\"}}},  {\"bool\": {\"must\": [{ \"range\": { \"start\": { \"lt\": \"now\" } } },{ \"range\": { \"stop\": { \"gt\": \"now\" } } }]}}  ]}}]}}")
	public Page<ProgrammeDoc> findInTimeSlot(String channel, ZonedDateTime begin, ZonedDateTime end, Pageable pageable);

	@Query("{\"bool\":{\"must\":[{\"query_string\":{\"default_operator\":\"and\",\"fields\":[\"channel\"],\"query\":\"?0\"}},{\"range\":{\"start\":{\"lte\":\"?1\"}}},{\"range\":{\"stop\":{\"gte\":\"?1\"}}}]}}")
	public Optional<ProgrammeDoc> findNow(String channel, ZonedDateTime now);

	public Optional<ProgrammeDoc> findByChannelAndStart(String channel, ZonedDateTime start);

	public Page<ProgrammeDoc> findByDescriptionContaining(String description, Pageable pageable);

	public Page<ProgrammeDoc> findByDescriptionContainingAndDescriptionLang(String description, String descriptionLang,
			Pageable pageable);
}