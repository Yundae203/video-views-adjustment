package personal.streaming.application.common.batch.chunk.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import personal.streaming.application.common.batch.dto.ContentTotalStatisticsDto;
import personal.streaming.application.common.batch.dto.ContentDailyStatisticsDto;
import personal.streaming.application.common.batch.dto.HistoryAggregation;
import personal.streaming.application.common.batch.util.CalculateIncomeFromViews;
import personal.streaming.content_post_watch_history.repository.QContentPostWatchHistoryRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class ContentStatisticsProcessor implements ItemProcessor<ContentTotalStatisticsDto, ContentDailyStatisticsDto> {

    private final QContentPostWatchHistoryRepository qContentPostWatchHistoryRepository;

    @Value("#{jobParameters['today']}")
    private String today;

    @Override
    public ContentDailyStatisticsDto process(ContentTotalStatisticsDto item) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate watchedDate = LocalDate.parse(today, formatter);

        ContentDailyStatisticsDto dto
                = new ContentDailyStatisticsDto(item.getContentPostId(), item.getUserId(), watchedDate, item);

        // DB 에서 sum 한 값 매핑해서 select
        HistoryAggregation aggregate = qContentPostWatchHistoryRepository.aggregateHistoryById(item.getContentPostId(), watchedDate);

        // 일일 조회수 재생 시간 정산
        dto.countingViewsAndPlaytime(aggregate);

        // 수입 정산
        CalculateIncomeFromViews.updateCalculatedIncomeAndTotalViews(item, dto);

        return dto;
    }

}
