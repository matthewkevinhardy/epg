package epg.repos;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = { Config.class })
class ProgrammeRepoTest {

	@Autowired
	ProgrammeRepo programmeRepo;

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
