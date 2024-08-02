package epg.search.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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

	@Autowired
	private ProgrammeService programmeService;

	@GetMapping(path = "/description/{desc}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ProgrammeDoc>> byDescription(@PathVariable String desc,
			@RequestParam(required = false) String lang, Pageable pageable) {

		return ResponseEntity.ok(programmeService.byDescription(desc, lang, pageable));
	}

	@GetMapping(path = "/id/{id}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ProgrammeDoc> byId(@PathVariable String id) {

		return ResponseEntity.of(programmeService.byId(id));
	}

	@GetMapping(path = "/{channel}/nextHour", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ProgrammeDoc>> byChannel(@PathVariable String channel,
			@PageableDefault(sort = { "start" }, direction = Direction.ASC) final Pageable pageable) {

		return ResponseEntity.ok(programmeService.nextHour(channel, pageable));

	}

	@GetMapping(path = "/{channel}/now", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ProgrammeDoc>> now(@PathVariable String channel, Pageable pageable) {

		return ResponseEntity.ok(programmeService.now(channel, pageable));
	}

	@GetMapping(path = "/{channel}/today", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ProgrammeDoc>> today(@PathVariable String channel,
			@PageableDefault(sort = { "start" }, direction = Direction.ASC) final Pageable pageable) {

		return ResponseEntity.ok(programmeService.today(channel, pageable));
	}
}