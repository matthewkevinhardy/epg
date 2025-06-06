package epg.repos;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

import epg.documents.ProgrammeDoc;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProgrammeRepo extends ReactiveElasticsearchRepository<ProgrammeDoc, String> {

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
	public Flux<ProgrammeDoc> findInTimeSlot(String channel, ZonedDateTime begin, ZonedDateTime end, Sort pageable);

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
	public Flux<ProgrammeDoc> findNow(List<String> channel);

	public Mono<ProgrammeDoc> findByChannelAndStart(String channel, ZonedDateTime start);

	public Flux<ProgrammeDoc> findByDescriptionContaining(String description, Pageable pageable);

	public Flux<ProgrammeDoc> findByDescriptionContainingAndDescriptionLang(String description, String descriptionLang,
			Pageable pageable);
}