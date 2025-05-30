package epg.repos;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

import epg.documents.ChannelDoc;
import reactor.core.publisher.Flux;

public interface ChannelRepo extends ReactiveElasticsearchRepository<ChannelDoc, String> {

	public Flux<ChannelDoc> findByDisplayNameContainingOrIdContaining(String searchTermName, String searchTermId,
			Pageable pageable);
}