package epg.search.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import epg.documents.ProgrammeDoc;
import epg.repos.ProgrammeRepo;

@Service
public class ProgrammeService {

	@Autowired
	private ProgrammeRepo programmeRepo;

	@Autowired
	private ElasticsearchOperations elasticsearchOperations;

	public Page<ProgrammeDoc> byDescription(String desc, String lang, Pageable pageable) {

		if (!TextUtils.isBlank(lang)) {
			return programmeRepo.findByDescriptionAndDescriptionLang(desc, lang, pageable);
		}

		return programmeRepo.findByDescription(desc, pageable);
	}

	public ResponseEntity<ProgrammeDoc> byId(String id) {

		// IndexQuery indexQuery = new IndexQueryBuilder().withId(id).build();

		// return ResponseEntity.ofNullable(elasticsearchOperations.get(id,
		// ProgrammeDoc.class));

		return ResponseEntity.of(programmeRepo.findById(id));
	}

	public Page<ProgrammeDoc> byChannel(String channel, Pageable pageable) {

		Criteria startCriteria = new Criteria("start").between(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
		Criteria endCriteria = new Criteria("stop").between(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
		Criteria criteria = new Criteria("channel").is(channel).subCriteria(startCriteria.or(endCriteria));

		Query searchQuery = new CriteriaQuery(criteria);
		searchQuery.setPageable(pageable);
		SearchHits<ProgrammeDoc> hits = elasticsearchOperations.search(searchQuery, ProgrammeDoc.class);

		List<ProgrammeDoc> progDocList = hits.stream().map(h -> h.getContent()).collect(Collectors.toList());
		Page<ProgrammeDoc> page = new PageImpl<>(progDocList, pageable, hits.getTotalHits());

		return page;

	}

	public Optional<ProgrammeDoc> now(String channel, Pageable pageable) {

		return programmeRepo.findByChannelAndStartLessThanAndStopGreaterThan(channel, ZonedDateTime.now(ZoneOffset.UTC),
				ZonedDateTime.now(ZoneOffset.UTC));
	}

	public Page<ProgrammeDoc> today(String channel, Pageable pageable) {

		return programmeRepo.findByChannelAndStartBetweenOrStopBetween(channel,
				ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ZoneOffset.UTC),
				ZonedDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT, ZoneOffset.UTC),
				ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ZoneOffset.UTC),
				ZonedDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT, ZoneOffset.UTC), pageable);
	}

	public Page<ProgrammeDoc> byNextHour(@PathVariable String channel, Pageable pageable) {

		return programmeRepo.findByChannel(channel, pageable);
	}
}
