package epg.repos;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import epg.documents.ProgrammeDoc;

public interface ProgrammeRepo extends ElasticsearchRepository<ProgrammeDoc, String> {

	@Query("""
			{"bool":{
				"must":[
					{"match":{"channel":"?0"}},
					{"bool":{
						"should":[
								{"range":{"start":{"gte":"?1","lte":"?2"}}},
								{"range":{"stop":{"gte":"?1","lte":"?2"}}},
								{"bool":
									{"must": [
											{ "range": { "start": { "lt": "?1"}}},
											{ "range": { "stop": { "gt": "?2"}}}
										]
									}
								}
							]
						}
					}
				]
			}
			}""")
	public Page<ProgrammeDoc> findInTimeSlot(String channel, ZonedDateTime begin, ZonedDateTime end, Pageable pageable);

	@Query("""
			{"bool":{
				"must":[
						{"terms":{"channel": ?0 }},
						{"range":{"start":{"lte":"now"}}},
						{"range":{"stop":{"gte":"now"}}}
					]
				}
			}
			""")
	public Page<ProgrammeDoc> findNow(List<String> channel, Pageable pageable);

	public Optional<ProgrammeDoc> findByChannelAndStart(String channel, ZonedDateTime start);

	public Page<ProgrammeDoc> findByDescriptionContaining(String description, Pageable pageable);

	public Page<ProgrammeDoc> findByDescriptionContainingAndDescriptionLang(String description, String descriptionLang,
			Pageable pageable);
}