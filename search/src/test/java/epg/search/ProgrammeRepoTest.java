package epg.search;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import epg.documents.ProgrammeDoc;
import epg.repos.ProgrammeRepo;
import epg.search.util.TestUtil;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProgrammeRepoTest {

	@Autowired
	ProgrammeRepo programmeRepo;

	TestUtil testUtil = new TestUtil();

	@BeforeAll
	void before() {
		testUtil.saveProgs(programmeRepo);
	}

	@Test
	void testNowRepo() {
		Page<ProgrammeDoc> page = programmeRepo.findNow(List.of("channel1"), Pageable.unpaged());
		assertEquals(1, page.getNumberOfElements());
		assertTrue(page.get().anyMatch(p -> "prog 1".equals(p.getDescription())));

		page = programmeRepo.findNow(List.of("channel1", "channel2"), Pageable.unpaged());
		assertEquals(2, page.getNumberOfElements());
		assertTrue(page.get().anyMatch(p -> "prog 1".equals(p.getDescription())));
		assertTrue(page.get().anyMatch(p -> "prog 4".equals(p.getDescription())));
	}

	@Test
	void testOneProg() {

		Page<ProgrammeDoc> page = programmeRepo.findInTimeSlot("channel1",
				ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(0).withNano(0),
				ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(11).withNano(0),
				org.springframework.data.domain.Pageable.unpaged());
		assertEquals(1, page.getNumberOfElements());
		assertEquals("prog 1", page.getContent().get(0).getDescription());

		page = programmeRepo.findInTimeSlot("channel1", ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(85).withNano(0),
				ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(125).withNano(0), Pageable.unpaged());
		assertEquals(1, page.getNumberOfElements());
		assertEquals("prog 3", page.getContent().get(0).getDescription());
	}

	@Test
	void testTwoProgs() {

		Page<ProgrammeDoc> page = programmeRepo.findInTimeSlot("channel1",
				ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(0).withNano(0),
				ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(50).withNano(0),
				org.springframework.data.domain.Pageable.unpaged());

		assertEquals(2, page.getNumberOfElements());
	}

	@AfterAll
	void afterAll() {
		testUtil.deleteProgs(programmeRepo);
	}
}
