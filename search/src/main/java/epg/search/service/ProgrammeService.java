package epg.search.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import epg.documents.ProgrammeDoc;
import epg.repos.ProgrammeRepo;
import epg.search.cache.ProgDocList;
import epg.search.utils.QueryUtils;

@Service
public class ProgrammeService {

	@Autowired
	private ProgrammeRepo programmeRepo;

	public Page<ProgrammeDoc> byDescription(String desc, String lang, Pageable pageable) {

		if (!TextUtils.isBlank(lang)) {
			return programmeRepo.findByDescriptionContainingAndDescriptionLang(desc, lang, pageable);
		}

		return programmeRepo.findByDescriptionContaining(desc, pageable);
	}

	public Page<ProgrammeDoc> nextHour(String channel, Pageable pageable) {

		return programmeRepo.findInTimeSlot(QueryUtils.escape(channel), ZonedDateTime.now(ZoneOffset.UTC).withNano(0),
				ZonedDateTime.now(ZoneOffset.UTC).plusHours(1).withNano(0), pageable);

	}

	@Cacheable(value = "nowCache", unless = "#result==null")
	public Optional<ProgrammeDoc> now(String channel) {
		return programmeRepo.findNow(List.of(QueryUtils.escape(channel)), Pageable.unpaged()).get().findFirst();
	}

	public Page<ProgrammeDoc> now(List<String> channels, Pageable pageable) {
		return programmeRepo.findNow(channels.stream().map(c -> QueryUtils.escape(c)).toList(), pageable);
	}

	@Cacheable(value = "nowAndNextCache", unless = "#result.isEmpty")
	public ProgDocList nowAndNext(String channel) {

		ProgDocList nowNextList = new ProgDocList();

		programmeRepo.findNow(List.of(QueryUtils.escape(channel)), Pageable.unpaged()).get().findFirst()
				.ifPresent(nowDoc -> {
					nowNextList.add(nowDoc);
					programmeRepo.findByChannelAndStart(QueryUtils.escape(channel), nowDoc.getStop())
							.ifPresent(nextDoc -> {
								nowNextList.add(nextDoc);
							});
				});

		return nowNextList;
	}

	public Page<ProgrammeDoc> today(String channel, Pageable pageable) {

		return programmeRepo.findInTimeSlot(QueryUtils.escape(channel),
				ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ZoneOffset.UTC),
				ZonedDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT, ZoneOffset.UTC), pageable);
	}

}
