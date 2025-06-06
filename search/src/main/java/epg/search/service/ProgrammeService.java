package epg.search.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import epg.documents.ProgrammeDoc;
import epg.repos.ProgrammeRepo;
import epg.search.cache.ProgDocList;
import epg.search.utils.QueryUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProgrammeService {

	@Autowired
	private ProgrammeRepo programmeRepo;

	public Flux<ProgrammeDoc> byDescription(String desc, String lang, Pageable pageable) {

		if (!TextUtils.isBlank(lang)) {
			return programmeRepo.findByDescriptionContainingAndDescriptionLang(desc, lang, pageable);
		}

		return programmeRepo.findByDescriptionContaining(desc, pageable);
	}

	public Flux<ProgrammeDoc> nextHour(String channel, Pageable pageable) {

		return programmeRepo.findInTimeSlot(QueryUtils.escape(channel), ZonedDateTime.now(ZoneOffset.UTC).withNano(0),
				ZonedDateTime.now(ZoneOffset.UTC).plusHours(1).withNano(0), pageable.getSort());

	}

	// @Cacheable(value = "nowCache", unless = "#result==null")
	public Mono<ProgrammeDoc> now(String channel) {
		return programmeRepo.findNow(List.of(QueryUtils.escape(channel))).next();
	}

	public Flux<ProgrammeDoc> now(List<String> channels, Pageable pageable) {
		return programmeRepo.findNow(channels.stream().map(QueryUtils::escape).toList());
	}

	// @Cacheable(value = "nowAndNextCache", unless = "#result.isEmpty")
	public ProgDocList nowAndNext(String channel) {

		ProgDocList nowNextList = new ProgDocList();
		programmeRepo.findNow(List.of(QueryUtils.escape(channel))).map(nowResponse -> {
			nowNextList.add(nowResponse);
			programmeRepo.findByChannelAndStart(channel, nowResponse.getStop()).map(nextResponse -> {
				nowNextList.add(nextResponse);
				return nextResponse;
			});

			return nowResponse;
		});

		return nowNextList;
	}

	public Flux<ProgrammeDoc> today(String channel, Pageable pageable) {

		return programmeRepo.findInTimeSlot(QueryUtils.escape(channel),
				ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ZoneOffset.UTC),
				ZonedDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT, ZoneOffset.UTC), pageable.getSort());
	}

}
