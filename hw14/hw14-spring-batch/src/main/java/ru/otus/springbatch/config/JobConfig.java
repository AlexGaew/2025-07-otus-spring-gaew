package ru.otus.springbatch.config;

import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.springbatch.model.Notebook;
import ru.otus.springbatch.model.NotebookEntity;
import ru.otus.springbatch.repository.NotebookRepository;
import ru.otus.springbatch.service.NotebookMapperService;


@SuppressWarnings("unused")
@Configuration
@RequiredArgsConstructor
public class JobConfig {

  public static final String OUTPUT_FILE_NAME = "outputFileName";

  public static final String INPUT_FILE_NAME = "inputFileName";

  public static final String IMPORT_NOTEBOOK_JOB_NAME = "importNotebookJob";

  private static final int CHUNK_SIZE = 5;

  private final Logger logger = LoggerFactory.getLogger("Batch");

  private final JobRepository jobRepository;

  private final PlatformTransactionManager platformTransactionManager;

  @StepScope
  @Bean
  RepositoryItemReader<Notebook> mongoReader(NotebookRepository repository) {
    return new RepositoryItemReaderBuilder<Notebook>()
        .name("notebookReader")
        .repository(repository)
        .methodName("findAll")
        .sorts(Map.of("id", Direction.ASC)).build();
  }

  @StepScope
  @Bean
  public ItemProcessor<Notebook, NotebookEntity> processor(NotebookMapperService notebookMapperService) {
    return notebookMapperService::map;
  }

  @StepScope
  @Bean
  public JpaItemWriter<NotebookEntity> writer(EntityManagerFactory emf) {
    JpaItemWriter<NotebookEntity> writer = new JpaItemWriter<>();
    writer.setEntityManagerFactory(emf);
    return writer;
  }

  @Bean
  public Job importNotebookJob(Step transformNotebookStep) {
    return new JobBuilder(IMPORT_NOTEBOOK_JOB_NAME, jobRepository)
        .incrementer(new RunIdIncrementer())
        .flow(transformNotebookStep)
        .end()
        .listener(new JobExecutionListener() {
          @Override
          public void beforeJob(@NonNull JobExecution jobExecution) {
            logger.info("Начало job");
          }

          @Override
          public void afterJob(@NonNull JobExecution jobExecution) {
            logger.info("Конец job");
          }
        })
        .build();
  }

  @Bean
  public Step transformNotebookStep(ItemReader<Notebook> reader, JpaItemWriter<NotebookEntity> writer,
      ItemProcessor<Notebook, NotebookEntity> itemProcessor) {
    return new StepBuilder("transformNotebookStep", jobRepository)
        .<Notebook, NotebookEntity>chunk(CHUNK_SIZE, platformTransactionManager)
        .reader(reader)
        .processor(itemProcessor)
        .writer(writer)
        .listener(new ChunkListener() {
          public void beforeChunk(@NonNull ChunkContext chunkContext) {
            logger.info("Начало пачки");
          }

          public void afterChunk(@NonNull ChunkContext chunkContext) {
            logger.info("Конец пачки");
          }

          public void afterChunkError(@NonNull ChunkContext chunkContext) {
            logger.info("Ошибка пачки");
          }
        })
        .build();
  }

}
