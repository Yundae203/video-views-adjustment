package personal.streaming.application.common.batch.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentStatisticsListener {

    private final RedisTemplate<String, String> redisTemplate;
    private String key;

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        log.info("==== Job start ====");
        String today = jobExecution.getJobParameters().getString("today");
        key = "logged:time:" + today;
        jobExecution.getExecutionContext().put("redisKey", key);
        log.info("key = {}", key);
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        LocalDateTime startTime = jobExecution.getCreateTime();
        LocalDateTime endTime = jobExecution.getEndTime();

        Duration duration = Duration.between(startTime, endTime);
        long milliseconds = duration.toMillis();
        log.info("Job duration: {} ms", milliseconds);
        log.info("==== Job end ====");
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("==== Step start ====");
        Long end = redisTemplate.opsForZSet().size(key);
        if (end == null) {
            end = 0L;
        }
        stepExecution.getExecutionContext().putLong("start", 0L);
        stepExecution.getExecutionContext().putLong("end", end);
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        log.info("==== Step end ====");
    }
}
