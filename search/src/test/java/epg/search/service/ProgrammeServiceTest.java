package epg.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import epg.documents.ProgrammeDoc;
import epg.repos.ProgrammeRepo;
import epg.search.cache.ProgDocList;
import epg.search.util.TestUtil;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProgrammeServiceTest {

	static final Logger LOG = LogManager.getLogger(ProgrammeServiceTest.class);

	@Autowired
	ProgrammeRepo programmeRepo;

	@Autowired
	ProgrammeService programmeService;

	TestUtil testUtil = new TestUtil();

	@BeforeAll
	void before() {
		testUtil.saveProgs(programmeRepo);
	}

	@Test
	void testNow() {
		Optional<ProgrammeDoc> optionalProg = programmeService.now("channel1");
		assertTrue(optionalProg.isPresent());
		assertEquals("prog 1", optionalProg.get().getDescription());
	}

	@Test
	void testNowEmpty() {
		Optional<ProgrammeDoc> optionalProg = programmeService.now("channel321");
		assertTrue(optionalProg.isEmpty());
	}

	@Test
	void testNowAndNext() {
		ProgDocList nowAndNextList = programmeService.nowAndNext("channel1");
		assertEquals(2, nowAndNextList.size());
		assertTrue(nowAndNextList.stream().anyMatch(p -> "prog 1".equals(p.getDescription())));
		assertTrue(nowAndNextList.stream().anyMatch(p -> "prog 2".equals(p.getDescription())));
	}

	@AfterAll
	void afterAll() {
		testUtil.deleteProgs(programmeRepo);
	}

}
