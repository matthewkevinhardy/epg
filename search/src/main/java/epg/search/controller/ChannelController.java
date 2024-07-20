package epg.search.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import epg.documents.ChannelDoc;
import epg.repos.ChannelRepo;
import epg.search.service.ChannelService;

@RestController
@RequestMapping("/v1/channel")
public class ChannelController {

	private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager
			.getLogger(ChannelController.class);

	@Autowired
	private ChannelRepo channelRepo;

	@Autowired
	private ChannelService channelService;

	@GetMapping(path = "/{channel}", produces = APPLICATION_JSON_VALUE)
	public List<ChannelDoc> byChannel(@PathVariable String channel, Pageable pageable) {

		return channelService.findByDisplayName(channel);
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	public Page<ChannelDoc> all(Pageable pageable) {

		return channelRepo.findAll(pageable);
	}
}