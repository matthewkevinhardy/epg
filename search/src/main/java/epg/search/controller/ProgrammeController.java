package epg.search.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import epg.documents.ProgrammeDoc;
import epg.search.service.ProgrammeService;

@RestController
@RequestMapping("/v1/programme")
public class ProgrammeController {

	private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager
			.getLogger(ProgrammeController.class);

	@Autowired
	private ProgrammeService programmeService;

	@GetMapping(path = "/description/{desc}", produces = APPLICATION_JSON_VALUE)
	public Page<ProgrammeDoc> byDescription(@PathVariable String desc, @RequestParam(required = false) String lang,
			Pageable pageable) {

		return programmeService.byDescription(desc, lang, pageable);
	}

	@GetMapping(path = "/id/{id}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ProgrammeDoc> byId(@PathVariable String id) {

		return programmeService.byId(id);
	}

	@GetMapping(path = "/{channel}/nextHour", produces = APPLICATION_JSON_VALUE)
	public Page<ProgrammeDoc> byChannel(@PathVariable String channel, Pageable pageable) {

		return programmeService.byChannel(channel, pageable);

	}

	@GetMapping(path = "/{channel}/now", produces = APPLICATION_JSON_VALUE)
	public Optional<ProgrammeDoc> now(@PathVariable String channel, Pageable pageable) {

		return programmeService.now(channel, pageable);
	}

	@GetMapping(path = "/{channel}/today", produces = APPLICATION_JSON_VALUE)
	public Page<ProgrammeDoc> today(@PathVariable String channel, Pageable pageable) {

		return programmeService.today(channel, pageable);
	}

	@GetMapping(path = "/{channel}", produces = APPLICATION_JSON_VALUE)
	public Page<ProgrammeDoc> byNextHour(@PathVariable String channel, Pageable pageable) {

		return programmeService.byChannel(channel, pageable);
	}
}