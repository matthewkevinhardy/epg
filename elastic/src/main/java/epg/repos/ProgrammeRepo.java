package epg.repos;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import epg.documents.ProgrammeDoc;

public interface ProgrammeRepo extends ElasticsearchRepository<ProgrammeDoc, String> {

}