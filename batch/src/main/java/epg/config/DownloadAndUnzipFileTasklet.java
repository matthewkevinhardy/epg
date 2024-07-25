package epg.config;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class DownloadAndUnzipFileTasklet implements Tasklet {

	private String serverDir;
	private String fileName;
	private String importDir;

	public DownloadAndUnzipFileTasklet(String serverDir, String fileName, String importDir) {
		this.serverDir = serverDir;
		this.fileName = fileName;
		this.importDir = importDir;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		try {
			URL fileUrl = new URL(serverDir + "/" + fileName);
			File gzFile = new File(importDir + "/" + fileName);
			File unzipFile = new File(importDir + "/" + fileName.replace(".gz", ""));
			FileUtils.copyURLToFile(fileUrl, gzFile);

			FileInputStream fis = new FileInputStream(gzFile);
			GZIPInputStream gis = new GZIPInputStream(fis);
			FileUtils.copyInputStreamToFile(gis, unzipFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return RepeatStatus.FINISHED;
	}

}
