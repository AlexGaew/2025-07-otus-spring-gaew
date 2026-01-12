package ru.otus.springbatch.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.otus.springbatch.model.NotebookEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.springbatch.config.JobConfig.IMPORT_NOTEBOOK_JOB_NAME;

@SpringBootTest
@SpringBatchTest
@TestPropertySource(properties = {
        "mongock.change-logs-scan-package=ru.otus.springbatch.testchangelogs"
})
class MigrateToSqlJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testJob() throws Exception {
        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(IMPORT_NOTEBOOK_JOB_NAME);

        JobParameters parameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(parameters);

        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        List<NotebookEntity> notebooks = entityManager
                .createQuery("SELECT n FROM NotebookEntity n", NotebookEntity.class)
                .getResultList();

        assertThat(notebooks).isNotEmpty();
        assertThat(notebooks).hasSizeGreaterThan(0);
    }
}