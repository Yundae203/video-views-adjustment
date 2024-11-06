package personal.streaming.application.common.batch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import personal.streaming.application.common.batch.chunk.processor.DailyStatisticsProcessor;
import personal.streaming.application.common.batch.chunk.processor.TotalStatisticsProcessor;
import personal.streaming.application.common.batch.chunk.processor.VerifyTotalStatisticsProcessor;
import personal.streaming.application.common.batch.chunk.reader.LoggedContentReader;
import personal.streaming.application.common.batch.chunk.reader.VerifyDailyStatisticsReader;
import personal.streaming.application.common.batch.chunk.reader.VerifyTotalStatisticsReader;
import personal.streaming.application.common.batch.chunk.writer.DailyStatisticsBatchDeleteWriter;
import personal.streaming.application.common.batch.chunk.writer.DailyStatisticsBatchWriter;
import personal.streaming.application.common.batch.chunk.writer.TotalStatisticsBatchWriter;
import personal.streaming.application.common.batch.dto.ContentDailyStatisticsSimpleDto;
import personal.streaming.application.common.batch.listener.DailyStatisticsStepListener;
import personal.streaming.application.common.batch.dto.ContentTotalStatisticsDto;
import personal.streaming.application.common.batch.dto.ContentDailyStatisticsDto;
import personal.streaming.application.common.batch.incrementer.LocalDateIncrementer;

@Configuration
@RequiredArgsConstructor
public class ContentStatisticsJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final LocalDateIncrementer localDateIncrementer;

    private final DailyStatisticsStepListener dailyStatisticsStepListener;

    // reader
    private final LoggedContentReader loggedContentReader;
    private final VerifyTotalStatisticsReader verifyTotalStatisticsReader;
    private final VerifyDailyStatisticsReader verifyDailyStatisticsReader;

    // processor
    private final DailyStatisticsProcessor dailyStatisticsProcessor;
    private final TotalStatisticsProcessor totalStatisticsProcessor;
    private final VerifyTotalStatisticsProcessor verifyTotalStatisticsProcessor;

    // writer
    private final DailyStatisticsBatchWriter dailyStatisticsBatchWriter;
    private final DailyStatisticsBatchDeleteWriter dailyStatisticsBatchDeleteWriter;
    private final TotalStatisticsBatchWriter totalStatisticsBatchWriter;

    @Value("${spring.chunk.size}")
    private int chunkSize;

    @Bean
    public Job job() {
        return new JobBuilder("content-statistics-job", jobRepository)
                .incrementer(localDateIncrementer)
                .start(verifyTotalStatisticsStep())
                .next(verifyDailyStatisticsStep())
                .next(dailyStatisticsStep())
                .next(totalStatisticsStep())
                .build();
    }

    @Bean
    public Step verifyTotalStatisticsStep() {
        return new StepBuilder("verify-total-statistics-step", jobRepository)
                .<ContentTotalStatisticsDto, ContentTotalStatisticsDto>chunk(chunkSize, transactionManager)
                .reader(verifyTotalStatisticsReader)
                .processor(verifyTotalStatisticsProcessor)
                .writer(totalStatisticsBatchWriter)
                .build();
    }

    @Bean
    public Step verifyDailyStatisticsStep() {
        return new StepBuilder("verify-daily-statistics-step", jobRepository)
                .<ContentDailyStatisticsSimpleDto, ContentDailyStatisticsSimpleDto>chunk(chunkSize, transactionManager)
                .reader(verifyDailyStatisticsReader)
                .writer(dailyStatisticsBatchDeleteWriter)
                .build();
    }

    @Bean
    public Step dailyStatisticsStep() {
        return new StepBuilder("daily-statistics-step", jobRepository)
                .listener(dailyStatisticsStepListener)
                .<ContentTotalStatisticsDto, ContentDailyStatisticsDto>chunk(chunkSize, transactionManager)
                .reader(loggedContentReader)
                .processor(dailyStatisticsProcessor)
                .writer(dailyStatisticsBatchWriter)
                .build();
    }

    @Bean
    public Step totalStatisticsStep() {
        return new StepBuilder("total-statistics-step", jobRepository)
                .listener(dailyStatisticsStepListener)
                .<ContentTotalStatisticsDto, ContentTotalStatisticsDto>chunk(chunkSize, transactionManager)
                .reader(loggedContentReader)
                .processor(totalStatisticsProcessor)
                .writer(totalStatisticsBatchWriter)
                .build();
    }
}
