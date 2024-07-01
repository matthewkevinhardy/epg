package epg;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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
import epg.xml.Icon;
import epg.xml.Programme;

@SpringBootApplication
public class BatchFileImport {

	@Autowired
	private ChannelRepo channelRepo;

	@Autowired
	private ProgrammeRepo programmeRepo;

	@Value("${epg.batch.importFile}")
	private Resource importFile;

	// 2024 07 03 223000 +0000
	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss Z");

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(BatchFileImport.class, args)));
	}

	@Bean
	public Job importFileJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//		Step importChannelsStep = importStep(jobRepository, transactionManager, channelReader(importFile),
//				writer(channelRepo), channelProcessor(), "importChannelsStep");

		Step importProgsStep = importStep(jobRepository, transactionManager, progReader(importFile),
				writer(programmeRepo), progProcessor(), "importChannelsStep");

		return new JobBuilder("importFileJob", jobRepository)
				.start(deleteStep(jobRepository, transactionManager, programmeRepo, "delete prog"))
				.next(importProgsStep).build();

	}

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

	private Set<String> keys = new HashSet<>();
	private int counter = 0;

	private ItemProcessor<Programme, ProgrammeDoc> progProcessor() {
		return new ItemProcessor<Programme, ProgrammeDoc>() {

			@Override
			public ProgrammeDoc process(Programme item) throws Exception {
				ProgrammeDoc doc = new ProgrammeDoc();

				doc.setProgId(item.getChannel() + item.getStart() + item.getStop());

				if (keys.add(doc.getProgId())) {
					counter++;
				} else {
					System.err.println(counter++);
				}

				if (item.getCatagories() != null)
					doc.setCategory(item.getCatagories().stream().map(Category::getValue).collect(Collectors.toList()));
				doc.setChannel(item.getChannel());
				// doc.getCountry(item.get)
				if (item.getDescription() != null)
					doc.setDescription(item.getDescription().getValue());
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

				return doc;
			}
		};
	}

	private <I, O> Step importStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			ItemReader<I> reader, ItemWriter<O> writer, ItemProcessor<I, O> itemProcessor, String stepName) {

		return new StepBuilder(stepName, jobRepository).<I, O>chunk(1000, transactionManager).processor(itemProcessor)
				.reader(reader).writer(writer).build();
	}

	private ItemReader<Channel> channelReader(Resource inputFile) {

		Jaxb2Marshaller channelMarshaller = new Jaxb2Marshaller();
		channelMarshaller.setClassesToBeBound(Channel.class, Icon.class);
		channelMarshaller.setMappedClass(Channel.class);

		return new StaxEventItemReaderBuilder<Channel>().name("channelReader").resource(inputFile)
				.addFragmentRootElements("channel").unmarshaller(channelMarshaller).build();
	}

	private ItemReader<Programme> progReader(Resource inputFile) {

		Jaxb2Marshaller channelMarshaller = new Jaxb2Marshaller();
		channelMarshaller.setClassesToBeBound(Programme.class, Icon.class);
		channelMarshaller.setMappedClass(Programme.class);

		return new StaxEventItemReaderBuilder<Programme>().name("progReader").resource(inputFile)
				.addFragmentRootElements("programme").unmarshaller(channelMarshaller).build();
	}

	private <T, ID> ItemWriter<T> writer(ElasticsearchRepository<T, ID> repo) {
		return new ItemWriter<T>() {

			@Override
			public void write(Chunk<? extends T> chunk) throws Exception {
				repo.saveAll(chunk);
			}
		};
	}

	private <T, ID> Step deleteStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			ElasticsearchRepository<T, ID> repo, String stepName) {

		return new StepBuilder(stepName, jobRepository).tasklet(new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				repo.deleteAll();
				return RepeatStatus.FINISHED;
			}
		}, transactionManager).build();
	}
}
