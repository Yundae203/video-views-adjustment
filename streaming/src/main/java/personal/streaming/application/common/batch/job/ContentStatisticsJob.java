package personal.streaming.application.common.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import personal.streaming.application.common.batch.dto.ContentTotalStatisticsDto;
import personal.streaming.application.common.batch.dto.ContentDailyStatisticsDto;
import personal.streaming.application.common.batch.incrementer.LocalDateIncrementer;
import personal.streaming.application.common.batch.listener.ContentStatisticsListener;

@Configuration
@RequiredArgsConstructor
public class ContentStatisticsJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final LocalDateIncrementer localDateIncrementer;

    private final ContentStatisticsListener contentStatisticsListener;

    private final ItemReader<ContentTotalStatisticsDto> contentIdRedisReader;
    private final ItemProcessor<ContentTotalStatisticsDto, ContentDailyStatisticsDto> contentStatisticsProcessor;
    private final ItemWriter<ContentDailyStatisticsDto> contentBatchWriter;

    @Value("${spring.batch.size}")
    private int batchSize;

    @Bean
    public Job job() {
        return new JobBuilder("content-statistics-job", jobRepository)
                .incrementer(localDateIncrementer)
                .listener(contentStatisticsListener)
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return new StepBuilder("content-statistics-step", jobRepository)
                .<ContentTotalStatisticsDto, ContentDailyStatisticsDto>chunk(batchSize, transactionManager)
                .listener(contentStatisticsListener)
                .reader(contentIdRedisReader)
                .processor(contentStatisticsProcessor)
                .writer(contentBatchWriter)
                .build();
    }
}
