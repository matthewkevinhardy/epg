package epg.search.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import epg.documents.ChannelDoc;
import epg.search.service.ChannelService;

@RestController
@RequestMapping("/v1/channel")
public class ChannelController {

	private static final Logger LOG = LogManager.getLogger(ChannelController.class);

	@Autowired
	private ChannelService channelService;

	@GetMapping(path = "/{searchTerm}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ChannelDoc>> byChannel(@PathVariable String searchTerm,
			@PageableDefault(sort = { "id" }, direction = Direction.ASC) final Pageable pageable) {

		return ResponseEntity.ok(channelService.findByDisplayNameContaining(searchTerm, pageable));
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ChannelDoc>> findAll(
			@PageableDefault(sort = { "id" }, direction = Direction.ASC) final Pageable pageable) {

		return ResponseEntity.ok(channelService.findAll(pageable));
	}
}