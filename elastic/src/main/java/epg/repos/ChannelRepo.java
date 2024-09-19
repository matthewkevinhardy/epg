package epg.repos;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import epg.documents.ChannelDoc;

public interface ChannelRepo extends ElasticsearchRepository<ChannelDoc, String> {

	public Page<ChannelDoc> findByDisplayNameContaining(String term, Pageable pageable);

	public Page<ChannelDoc> findByDisplayNameContaining(List<String> terms, Pageable pageable);

}