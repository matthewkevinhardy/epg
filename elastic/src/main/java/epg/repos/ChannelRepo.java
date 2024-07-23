package epg.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import epg.documents.ChannelDoc;

public interface ChannelRepo extends ElasticsearchRepository<ChannelDoc, String> {

	public Page<ChannelDoc> findByDisplayName(String displayName, Pageable pageable);

	@Query("{\"match\": {\"displayName\": {\"query\":\"?0\", \"fuzziness\": \"3\"} }}")
	public Page<ChannelDoc> findFuzzyDisplayName(String displayName, Pageable pageable);

}