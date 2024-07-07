package epg.service.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import epg.documents.ProgrammeDoc;
import epg.repos.ProgrammeRepo;

@RestController
@RequestMapping("/v1/programme")
public class ProgrammeController {

	private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager
			.getLogger(ProgrammeController.class);

	@Autowired
	private ProgrammeRepo programmeRepo;

	@GetMapping(path = "/description/{desc}", produces = APPLICATION_JSON_VALUE)
	public Page<ProgrammeDoc> byDescription(@PathVariable String desc, @RequestParam(required = false) String lang,
			Pageable pageable) {

		if (!TextUtils.isBlank(lang)) {
			return programmeRepo.findByDescriptionAndDescriptionLang(desc, lang, pageable);
		}

		return programmeRepo.findByDescription(desc, pageable);
	}

	@GetMapping(path = "/{channel}/nextHour", produces = APPLICATION_JSON_VALUE)
	public Page<ProgrammeDoc> byChannel(@PathVariable String channel, Pageable pageable) {
		return programmeRepo.findByChannelAndStartBetweenOrStopBetween(channel, LocalDateTime.now(),
				LocalDateTime.now().plusHours(1), LocalDateTime.now(), LocalDateTime.now().plusHours(1), pageable);

	}

	@GetMapping(path = "/{channel}/now", produces = APPLICATION_JSON_VALUE)
	public Optional<ProgrammeDoc> now(@PathVariable String channel, Pageable pageable) {

		return programmeRepo.findByChannelAndStartLessThanAndStopGreaterThan(channel, LocalDateTime.now(),
				LocalDateTime.now());
	}

	@GetMapping(path = "/{channel}/today", produces = APPLICATION_JSON_VALUE)
	public Page<ProgrammeDoc> today(@PathVariable String channel, Pageable pageable) {

		return programmeRepo.findByChannelAndStartBetweenOrStopBetween(channel,
				LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT),
				LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT),
				LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT),
				LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT), pageable);
	}

	@GetMapping(path = "/{channel}", produces = APPLICATION_JSON_VALUE)
	public Page<ProgrammeDoc> byNextHour(@PathVariable String channel, Pageable pageable) {

		return programmeRepo.findByChannel(channel, pageable);
	}
}