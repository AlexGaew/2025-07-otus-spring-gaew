package ru.otus.springbatch.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@RequiredArgsConstructor
@ShellComponent
public class BatchCommands {


    private final Job importNotebookJob;

    private final JobLauncher jobLauncher;


    //http://localhost:8080/h2-console/


    @SuppressWarnings("unused")
    @ShellMethod(value = "startMigrationJobWithJobLauncher", key = "sm-jl")
    public void startMigrationJobWithJobLauncher() throws Exception {
        JobExecution execution = jobLauncher.run(importNotebookJob, new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters());
        System.out.println(execution);
    }

}
