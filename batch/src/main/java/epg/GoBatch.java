package epg;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class GoBatch implements CommandLineRunner {

	private final JobLauncher jobLauncher;
	private final ApplicationContext applicationContext;

	public GoBatch(JobLauncher jobLauncher, ApplicationContext applicationContext) {
		this.jobLauncher = jobLauncher;
		this.applicationContext = applicationContext;
	}

	public static void main(String[] args) {
		SpringApplication.run(GoBatch.class, args).close();
	}

	@Override
	public void run(String... args) throws Exception {
		Job job = (Job) applicationContext.getBean("importFilesJob");

		JobParameters jobParameters = new JobParametersBuilder()
				.addString("importFilesJob", String.valueOf(System.currentTimeMillis())).toJobParameters();

		var jobExecution = jobLauncher.run(job, jobParameters);

	}
}
