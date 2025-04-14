package epg.batch.config;

import java.io.File;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Conflicts;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.json.JsonData;
import epg.batch.config.tasklet.DownloadAndUnzipFileTasklet;
import epg.batch.config.xml.Category;
import epg.batch.config.xml.Channel;
import epg.batch.config.xml.Credits;
import epg.batch.config.xml.Icon;
import epg.batch.config.xml.Programme;
import epg.documents.ChannelDoc;
import epg.documents.ProgrammeDoc;
import epg.repos.ChannelRepo;
import epg.repos.ProgrammeRepo;

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

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss Z");

	@Bean
	public Job importFilesJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

		SimpleJobBuilder simpleJobBuilder = new JobBuilder("importFileJob", jobRepository)
				.start(deleteOldProgsStep(jobRepository, transactionManager));

		importFiles.stream().forEach(fileName -> {
			try {
				Step downloadFileStep = new StepBuilder("download " + fileName, jobRepository)
						.tasklet(new DownloadAndUnzipFileTasklet(serverDir, fileName, importDir), transactionManager)
						.build();

				Step importChannelsStep = importStep(jobRepository, transactionManager, channelReader(fileName),
						writer(channelRepo), channelProcessor(), "importChannelsStep: " + fileName);

				Step importProgsStep = importStep(jobRepository, transactionManager, progReader(fileName),
						writer(programmeRepo), progProcessor(), "importProgsStep: " + fileName);

				simpleJobBuilder.next(downloadFileStep);
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
			public ProgrammeDoc process(Programme item) {
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
				doc.setStart(
						ZonedDateTime.parse(item.getStart(), dateTimeFormatter).withZoneSameInstant(ZoneOffset.UTC));
				doc.setStop(ZonedDateTime.parse(item.getStop(), dateTimeFormatter).withZoneSameInstant(ZoneOffset.UTC));
				doc.setTitle(item.getTitle().getValue());
				if (item.getCredits() != null)
					doc.setCredits(item.getCredits().stream().map(Credits::getActor).collect(Collectors.toList()));

				// Skip anything before today
				if (doc.getStop().isBefore(ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ZoneOffset.UTC))) {
					return null;
				}

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
	 * @throws MalformedURLException
	 */
	private ItemReader<Channel> channelReader(String fileName) throws MalformedURLException {

		FileUrlResource inputFile = new FileUrlResource(importDir + "/" + fileName.replace(".gz", ""));

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
	 * @throws MalformedURLException
	 */
	private ItemReader<Programme> progReader(String fileName) throws MalformedURLException {

		FileUrlResource inputFile = new FileUrlResource(importDir + "/" + fileName.replace(".gz", ""));

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

	/**
	 * Clear out old programmes
	 * 
	 * @param <T>
	 * @param <ID>
	 * @param jobRepository
	 * @param transactionManager
	 * @return
	 */
	private <T, ID> Step deleteOldProgsStep(JobRepository jobRepository,
			PlatformTransactionManager transactionManager) {

		return new StepBuilder("Delete Old Progs", jobRepository).tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				DeleteByQueryRequest dbyquery = DeleteByQueryRequest.of(fn -> fn.conflicts(Conflicts.Proceed)
						.query(RangeQuery.of(rq -> rq.field("stop").lt(JsonData.of("now/d")))._toQuery())
						.index("programme"));

				DeleteByQueryResponse dqr = elasticsearchClient.deleteByQuery(dbyquery);

				LOG.info("Deleted old progs: " + dqr.deleted());
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
