package epg;

import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GoBatch {

	@Autowired
	private Job importFilesJob;

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(GoBatch.class, args)));
	}

	@Bean
	public Job runNow() {
		return importFilesJob;
	}
}
