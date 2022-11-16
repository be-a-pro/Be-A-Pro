package com.beer.BeAPro.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final BatchConfig batchConfig;
    private final JobLauncher jobLauncher;


    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void updateUserData() throws InterruptedException {
        // job parameter 설정
        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(LocalDateTime.now().toString()));
        JobParameters jobParameters = new JobParameters(confMap);

        // batch 실행
        try{
            jobLauncher.run(batchConfig.updateUserJob(), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException |
                JobParametersInvalidException | JobRestartException e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void deleteProject() throws InterruptedException {
        // job parameter 설정
        Map<String, JobParameter> confMap = new HashMap<>();
        confMap.put("time", new JobParameter(LocalDateTime.now().toString()));
        JobParameters jobParameters = new JobParameters(confMap);

        // batch 실행
        try{
            jobLauncher.run(batchConfig.deleteProjectJob(), jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException |
                JobParametersInvalidException | JobRestartException e) {
            log.error(e.getMessage());
        }
    }
}
