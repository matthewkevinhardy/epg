package epg.search.util;

import org.apache.log4j.Logger;

import epg.documents.ProgrammeDoc;
import epg.repos.ProgrammeRepo;

public class TestUtil {
	static final Logger LOG = org.apache.log4j.LogManager.getLogger(TestUtil.class);

	ProgrammeDoc p1 = new ProgrammeDoc.ProgrammeBuilder().withChannel("channel1").withDesc("prog 1").withStartPlus(0)
			.withStopPlus(30).build();
	ProgrammeDoc p2 = new ProgrammeDoc.ProgrammeBuilder().withChannel("channel1").withDesc("prog 2").withStartPlus(30)
			.withStopPlus(60).build();
	ProgrammeDoc p3 = new ProgrammeDoc.ProgrammeBuilder().withChannel("channel1").withDesc("prog 3").withStartPlus(90)
			.withStopPlus(120).build();

	ProgrammeDoc p4 = new ProgrammeDoc.ProgrammeBuilder().withChannel("channel2").withDesc("prog 4").withStartPlus(0)
			.withStopPlus(30).build();
	ProgrammeDoc p5 = new ProgrammeDoc.ProgrammeBuilder().withChannel("channel2").withDesc("prog 5").withStartPlus(30)
			.withStopPlus(60).build();
	ProgrammeDoc p6 = new ProgrammeDoc.ProgrammeBuilder().withChannel("channel2").withDesc("prog 6").withStartPlus(90)
			.withStopPlus(120).build();

	public void saveProgs(ProgrammeRepo programmeRepo) {
		programmeRepo.save(p1);
		LOG.info(p1);
		programmeRepo.save(p2);
		programmeRepo.save(p3);
		programmeRepo.save(p4);
		programmeRepo.save(p5);
		programmeRepo.save(p6);
	}

	public void deleteProgs(ProgrammeRepo programmeRepo) {
		programmeRepo.deleteAll();
	}
}
