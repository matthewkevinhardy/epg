package epg.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import epg.documents.ChannelDoc;

public interface ChannelRepo extends ElasticsearchRepository<ChannelDoc, String> {

	public Page<ChannelDoc> findByDisplayNameContaining(String searchTerm, Pageable pageable);
}