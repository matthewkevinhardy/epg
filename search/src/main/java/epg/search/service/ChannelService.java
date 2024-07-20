package epg.search.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import epg.documents.ChannelDoc;
import epg.repos.ChannelRepo;

@Service
public class ChannelService {

	@Autowired
	private ChannelRepo channelRepos;

	@Autowired
	private ElasticsearchOperations elasticsearchOperations;

	public List<ChannelDoc> findByDisplayName(String displayName) {
		Criteria criteria = new Criteria("displayName").contains(displayName);
		Query searchQuery = new CriteriaQuery(criteria);
		SearchHits<ChannelDoc> hits = elasticsearchOperations.search(searchQuery, ChannelDoc.class);
		List<ChannelDoc> channelList = hits.stream().map(h -> h.getContent()).collect(Collectors.toList());
		return channelList;
	}
}
