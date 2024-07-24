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

	public Page<ChannelDoc> findByDisplayNameContaining(String displayName, Pageable pageable) {

		return channelRepo.findByDisplayNameContaining(displayName, pageable);
	}

	public Page<ChannelDoc> findAll(Pageable pageable) {

		return channelRepo.findAll(pageable);
	}
}
