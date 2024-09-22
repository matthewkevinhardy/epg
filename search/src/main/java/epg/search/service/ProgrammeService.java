package epg.search.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import epg.documents.ProgrammeDoc;
import epg.repos.ProgrammeRepo;

@Service
public class ProgrammeService {

	@Autowired
	private ProgrammeRepo programmeRepo;

	public Page<ProgrammeDoc> byDescription(String desc, String lang, Pageable pageable) {

		if (!TextUtils.isBlank(lang)) {
			return programmeRepo.findByDescriptionAndDescriptionLang(desc, lang, pageable);
		}

		return programmeRepo.findByDescription(desc, pageable);
	}

	public Optional<ProgrammeDoc> byId(String id) {

		return programmeRepo.findById(id);
	}

	public Page<ProgrammeDoc> nextHour(String channel, Pageable pageable) {

		return programmeRepo.findInTimeSlot(channel, ZonedDateTime.now(ZoneOffset.UTC).withNano(0),
				ZonedDateTime.now(ZoneOffset.UTC).plusHours(1).withNano(0), pageable);

	}

	public Optional<ProgrammeDoc> now(String channel) {

		return programmeRepo.findNow(channel);
	}

	public List<ProgrammeDoc> nowAndNext(String channel) {

		List<ProgrammeDoc> nowNextList = new LinkedList<>();

		programmeRepo.findNow(channel).ifPresent(nowDoc -> {
			nowNextList.add(nowDoc);
			programmeRepo.findByChannelAndStart(channel, nowDoc.getStop()).ifPresent(nextDoc -> {
				nowNextList.add(nextDoc);
			});
		});

		return nowNextList;
	}

	public Page<ProgrammeDoc> today(String channel, Pageable pageable) {

		return programmeRepo.findInTimeSlot(channel,
				ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ZoneOffset.UTC),
				ZonedDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT, ZoneOffset.UTC), pageable);
	}

}
