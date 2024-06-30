package epg;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
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
import epg.repos.ChannelRepo;
import epg.xml.Channel;
import epg.xml.DisplayName;

@SpringBootApplication
public class BatchFileImport {

	@Autowired
	private ChannelRepo channelRepo;

	@Value("${epg.batch.importFile}")
	private Resource importFile;

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(BatchFileImport.class, args)));
	}

	@Bean
	public Job importFileJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		Step importChannelsStep = importStep(jobRepository, transactionManager, channelReader(importFile),
				writer(channelRepo), channelProcessor(), "importChannelsStep");

		return new JobBuilder("importFileJob", jobRepository).start(importChannelsStep).build();

	}

	private ItemProcessor<Channel, ChannelDoc> channelProcessor() {
		return new ItemProcessor<Channel, ChannelDoc>() {

			@Override
			public ChannelDoc process(Channel item) throws Exception {
				ChannelDoc channelDoc = new ChannelDoc();
				channelDoc.setDisplayName(item.getDisplayName().getValue());
				channelDoc.setId(item.getDisplayName().getValue());
				channelDoc.setUrl(item.getUrl());
				return channelDoc;
			}
		};
	}

	private <I, O> Step importStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			ItemReader<I> reader, ItemWriter<O> writer, ItemProcessor<I, O> itemProcessor, String stepName) {

		return new StepBuilder(stepName, jobRepository).<I, O>chunk(10, transactionManager).processor(itemProcessor)
				.reader(reader).writer(writer).build();
	}

	private ItemReader<Channel> channelReader(Resource inputFile) {

		Jaxb2Marshaller channelMarshaller = new Jaxb2Marshaller();
		channelMarshaller.setClassesToBeBound(Channel.class, DisplayName.class);
		channelMarshaller.setMappedClass(Channel.class);
		// channelMarshaller.setPackagesToScan("epg.xml");

		return new StaxEventItemReaderBuilder<Channel>().name("channelReader").resource(inputFile)
				.addFragmentRootElements("channel").unmarshaller(channelMarshaller).build();
	}

	private <T, ID> ItemWriter<T> writer(ElasticsearchRepository<T, ID> repo) {
		return new ItemWriter<T>() {

			@Override
			public void write(Chunk<? extends T> chunk) throws Exception {
				repo.saveAll(chunk);
			}
		};
	}
}
