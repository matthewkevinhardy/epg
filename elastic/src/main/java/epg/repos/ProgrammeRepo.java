package epg.repos;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import epg.documents.Programme;

public interface ProgrammeRepo extends ElasticsearchRepository<Programme, String> {

}