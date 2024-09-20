package epg.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import epg.documents.ChannelDoc;
import epg.repos.ChannelRepo;

@Service
public class ChannelService {

	@Autowired
	private ChannelRepo channelRepo;

	public Page<ChannelDoc> findByDisplayNameContaining(String term, Pageable pageable) {

		return channelRepo.findByDisplayNameContaining(term, pageable);
	}

	public Page<ChannelDoc> findAll(Pageable pageable) {

		return channelRepo.findAll(pageable);
	}
}
