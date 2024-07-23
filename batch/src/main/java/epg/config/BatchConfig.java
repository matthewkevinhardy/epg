package epg.config;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import epg.documents.ChannelDoc;
import epg.documents.ProgrammeDoc;
import epg.repos.ChannelRepo;
import epg.repos.ProgrammeRepo;
import epg.xml.Category;
import epg.xml.Channel;
import epg.xml.Credits;
import epg.xml.Icon;
import epg.xml.Programme;

@Configuration
public class BatchConfig {
	private static final Logger LOG = LogManager.getLogger(BatchConfig.class);

	@Autowired
	private ChannelRepo channelRepo;

	@Autowired
	private ProgrammeRepo programmeRepo;

	@Value("${epg.batch.importFiles}")
	private List<String> importFiles;

	@Value("${epg.batch.importDir}")
	private String importDir;

	@Value("${epg.batch.server.dir}")
	private String serverDir;

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss Z");

	@Bean
	public Job importFilesJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		SimpleJobBuilder simpleJobBuilder = new JobBuilder("importFileJob", jobRepository)
				.start(downloadFilesAndUnzipStep(jobRepository, transactionManager))
				// .next(deleteRepoStep(jobRepository, transactionManager, programmeRepo,
				// "delete prog"))
				.next(deleteOldProgsStep(jobRepository, transactionManager));

		importFiles.stream().forEach(fileName -> {
			try {
				FileUrlResource file = new FileUrlResource(importDir + "/" + fileName.replace(".gz", ""));
				Step importChannelsStep = importStep(jobRepository, transactionManager, channelReader(file),
						writer(channelRepo), channelProcessor(), "importChannelsStep: " + fileName);
				Step importProgsStep = importStep(jobRepository, transactionManager, progReader(file),
						writer(programmeRepo), progProcessor(), "importProgsStep: " + fileName);

				simpleJobBuilder.next(importChannelsStep);
				simpleJobBuilder.next(importProgsStep);
			} catch (MalformedURLException e) {
				LOG.error(e);
			}

		});

		simpleJobBuilder.next(deleteFilesStep(jobRepository, transactionManager));

		return simpleJobBuilder.build();
	}

	/**
	 * Process channel xml
	 * 
	 * @return
	 */
	private ItemProcessor<Channel, ChannelDoc> channelProcessor() {
		return new ItemProcessor<Channel, ChannelDoc>() {

			@Override
			public ChannelDoc process(Channel item) throws Exception {
				ChannelDoc channelDoc = new ChannelDoc();
				channelDoc.setDisplayName(item.getDisplayName().getValue());
				channelDoc.setId(item.getId());
				channelDoc.setUrl(item.getUrl());
				return channelDoc;
			}
		};
	}

	/**
	 * Process programme xml
	 * 
	 * @return
	 */
	private ItemProcessor<Programme, ProgrammeDoc> progProcessor() {
		return new ItemProcessor<Programme, ProgrammeDoc>() {

			@Override
			public ProgrammeDoc process(Programme item) throws Exception {
				ProgrammeDoc doc = new ProgrammeDoc();

				doc.setProgId(item.getChannel() + item.getStart() + item.getStop());

				if (item.getCatagories() != null)
					doc.setCategory(item.getCatagories().stream().map(Category::getValue).collect(Collectors.toList()));
				doc.setChannel(item.getChannel());
				// doc.getCountry(item.get)
				if (item.getDescription() != null) {
					doc.setDescription(item.getDescription().getValue());
					doc.setDescriptionLang(item.getDescription().getLang());
				}
				if (item.getIcon() != null)
					doc.setIcon(item.getIcon().getSrc());
				// doc.setPreviouslyShown(item.get);
				if (item.getRating() != null) {
					doc.setRatingSystem(item.getRating().getSystem());
					doc.setRatingValue(item.getRating().getValue());
				}
				doc.setStart(LocalDateTime.parse(item.getStart(), dateTimeFormatter));
				doc.setStop(LocalDateTime.parse(item.getStop(), dateTimeFormatter));
				doc.setTitle(item.getTitle().getValue());
				if (item.getCredits() != null)
					doc.setCredits(item.getCredits().stream().map(Credits::getActor).collect(Collectors.toList()));

				return doc;
			}
		};
	}

	/**
	 * Create an import step
	 * 
	 * @param <I>
	 * @param <O>
	 * @param jobRepository
	 * @param transactionManager
	 * @param reader
	 * @param writer
	 * @param itemProcessor
	 * @param stepName
	 * @return
	 */
	private <I, O> Step importStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			ItemReader<I> reader, ItemWriter<O> writer, ItemProcessor<I, O> itemProcessor, String stepName) {

		return new StepBuilder(stepName, jobRepository).<I, O>chunk(1000, transactionManager).processor(itemProcessor)
				.reader(reader).writer(writer).build();
	}

	/**
	 * Read the channels xml
	 * 
	 * @param inputFile
	 * @return
	 */
	private ItemReader<Channel> channelReader(Resource inputFile) {

		Jaxb2Marshaller channelMarshaller = new Jaxb2Marshaller();
		channelMarshaller.setClassesToBeBound(Channel.class);
		channelMarshaller.setMappedClass(Channel.class);

		return new StaxEventItemReaderBuilder<Channel>().name("channelReader").resource(inputFile)
				.addFragmentRootElements("channel").unmarshaller(channelMarshaller).build();
	}

	/**
	 * Read the programmes xml
	 * 
	 * @param inputFile
	 * @return
	 */
	private ItemReader<Programme> progReader(Resource inputFile) {

		Jaxb2Marshaller channelMarshaller = new Jaxb2Marshaller();
		channelMarshaller.setClassesToBeBound(Programme.class, Icon.class);
		channelMarshaller.setMappedClass(Programme.class);

		return new StaxEventItemReaderBuilder<Programme>().name("progReader").resource(inputFile)
				.addFragmentRootElements("programme").unmarshaller(channelMarshaller).build();
	}

	/**
	 * Write to repo
	 * 
	 * @param <T>
	 * @param <ID>
	 * @param repo
	 * @return
	 */
	private <T, ID> ItemWriter<T> writer(ElasticsearchRepository<T, ID> repo) {
		return new ItemWriter<T>() {

			@Override
			public void write(Chunk<? extends T> chunk) throws Exception {
				repo.saveAll(chunk);
			}
		};
	}

	/**
	 * Clear out the repo
	 * 
	 * @param <T>
	 * @param <ID>
	 * @param jobRepository
	 * @param transactionManager
	 * @param repo
	 * @param stepName
	 * @return
	 */
	private <T, ID> Step deleteRepoStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			ElasticsearchRepository<T, ID> repo, String stepName) {

		return new StepBuilder(stepName, jobRepository).tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				repo.deleteAll();
				return RepeatStatus.FINISHED;
			}
		}, transactionManager).build();
	}

	private <T, ID> Step deleteOldProgsStep(JobRepository jobRepository,
			PlatformTransactionManager transactionManager) {

		return new StepBuilder("Delete Old Progs", jobRepository).tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				long count = programmeRepo.deleteByStopLessThan(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT));
				LOG.info("Deleted old progs: " + count);
				return RepeatStatus.FINISHED;
			}
		}, transactionManager).build();
	}

	/**
	 * Download file and unzip
	 * 
	 * @param <T>
	 * @param <ID>
	 * @param jobRepository
	 * @param transactionManager
	 * @return
	 */
	private <T, ID> Step downloadFilesAndUnzipStep(JobRepository jobRepository,
			PlatformTransactionManager transactionManager) {

		return new StepBuilder("download file", jobRepository).tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

				importFiles.stream().forEach(fileName -> {

					try {
						URL fileUrl = new URL(serverDir + "/" + fileName);
						File gzFile = new File(importDir + "/" + fileName);
						File unzipFile = new File(importDir + "/" + fileName.replace(".gz", ""));
						FileUtils.copyURLToFile(fileUrl, gzFile);

						FileInputStream fis = new FileInputStream(gzFile);
						GZIPInputStream gis = new GZIPInputStream(fis);
						FileUtils.copyInputStreamToFile(gis, unzipFile);

						LOG.info("Got file: " + unzipFile.getCanonicalPath());
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

				return RepeatStatus.FINISHED;
			}
		}, transactionManager).build();
	}

	/**
	 * Delete the files
	 * 
	 * @param <T>
	 * @param <ID>
	 * @param jobRepository
	 * @param transactionManager
	 * @return
	 */
	private <T, ID> Step deleteFilesStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		return new StepBuilder("delete file", jobRepository).tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				importFiles.stream().forEach(fileName -> {
					File gzFile = new File(importDir + "/" + fileName);
					File unzipFile = new File(importDir + "/" + fileName.replace(".gz", ""));
					gzFile.delete();
					LOG.info("Deleted file: " + gzFile.getAbsolutePath());
					unzipFile.delete();
					LOG.info("Deleted file: " + unzipFile.getAbsolutePath());
				});

				return RepeatStatus.FINISHED;
			}
		}, transactionManager).build();
	}
}
