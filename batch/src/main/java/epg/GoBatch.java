package epg;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class GoBatch implements CommandLineRunner {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(GoBatch.class, args).close();
	}

	@Override
	public void run(String... args) throws Exception {
		Job job = (Job) applicationContext.getBean("importFilesJob");

		JobParameters jobParameters = new JobParametersBuilder()
				.addString("Cmd line import files", String.valueOf(System.currentTimeMillis())).toJobParameters();

		var jobExecution = jobLauncher.run(job, jobParameters);

	}
}
