package epg.search.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

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
			@RequestParam(required = false) String lang,
			@PageableDefault(sort = { "start" }, direction = Direction.ASC) Pageable pageable) {

		return ResponseEntity.ok(programmeService.byDescription(desc, lang, pageable));
	}

	@GetMapping(path = "/nextHour", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ProgrammeDoc>> byChannel(@RequestParam(required = true) String channel,
			@PageableDefault(sort = { "start" }, direction = Direction.ASC) final Pageable pageable) {

		return ResponseEntity.ok(programmeService.nextHour(channel, pageable));

	}

	@GetMapping(path = "/now", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<ProgrammeDoc> now(@RequestParam(required = true) String channel, Pageable pageable) {
		return ResponseEntity.of(programmeService.now(channel));
	}

	@GetMapping(path = "/nowChannels", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ProgrammeDoc>> now(@RequestParam(required = true) List<String> channel,
			@PageableDefault(sort = { "channel" }, direction = Direction.ASC) final Pageable pageable) {
		return ResponseEntity.ok(programmeService.now(channel, pageable));
	}

	@GetMapping(path = "/nowAndNext", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProgrammeDoc>> nowAndNext(@RequestParam(required = true) String channel) {

		return ResponseEntity.ok(programmeService.nowAndNext(channel));
	}

	@GetMapping(path = "/today", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<ProgrammeDoc>> today(@RequestParam(required = true) String channel,
			@PageableDefault(sort = { "start" }, direction = Direction.ASC) final Pageable pageable) {

		return ResponseEntity.ok(programmeService.today(channel, pageable));
	}
}