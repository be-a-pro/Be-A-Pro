package com.beer.BeAPro.Configuration;


import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final UserService userService;


    @Bean
    public Job updateUserJob() {
        return jobBuilderFactory.get("updateUserJob")
                .start(updateUserStep(null))
                .build();
    }

    @Bean
    @JobScope // parameter
    public Step updateUserStep(@Value("#{jobParameters[time]}") String time) {
        return stepBuilderFactory.get("updateUserStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("===== Update User Step =====");
                    log.info("requestDate = {}", time);

                    // 휴면 계정으로 업데이트할 사용자 목록을 가져옴
                    List<User> usersToInactive = userService.findUserToInactive();
                    // 휴면 계정으로 변경
                    if (usersToInactive != null) {
                        usersToInactive.forEach(user -> System.out.println("user.getName() = " + user.getName()));
                        usersToInactive.forEach(userService::setInactive);
                    }
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
