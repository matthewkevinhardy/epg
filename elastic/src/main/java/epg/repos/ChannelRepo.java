package epg.repos;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import epg.documents.ChannelDoc;

public interface ChannelRepo extends ElasticsearchRepository<ChannelDoc, String> {

}