package epg.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import epg.documents.ChannelDoc;
import epg.repos.ChannelRepo;
import reactor.core.publisher.Flux;

@Service
public class ChannelService {

	@Autowired
	private ChannelRepo channelRepo;

	public Flux<ChannelDoc> findByDisplayNameContaining(String searchTerm, Pageable pageable) {

		return channelRepo.findByDisplayNameContainingOrIdContaining(searchTerm, searchTerm, pageable);
	}

	public Flux<ChannelDoc> findAll(Sort sort) {

		return channelRepo.findAll(sort);
	}
}
