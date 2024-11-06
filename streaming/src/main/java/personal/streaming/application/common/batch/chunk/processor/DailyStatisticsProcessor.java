package personal.streaming.application.common.batch.chunk.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import personal.streaming.application.common.batch.dto.ContentTotalStatisticsDto;
import personal.streaming.application.common.batch.dto.ContentDailyStatisticsDto;
import personal.streaming.application.common.batch.util.CalculateIncomeFromViews;
import personal.streaming.content_post_watch_history.domain.ContentPostWatchHistory;
import personal.streaming.content_post_watch_history.repository.QContentPostWatchHistoryRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class DailyStatisticsProcessor implements ItemProcessor<ContentTotalStatisticsDto, ContentDailyStatisticsDto> {

    private final QContentPostWatchHistoryRepository qContentPostWatchHistoryRepository;

    @Value("#{jobParameters['today']}")
    private String today;

    @Value("${spring.batch.size}")
    private int batchSize;

    @Override
    public ContentDailyStatisticsDto process(ContentTotalStatisticsDto item) {
        // JobParameter 에서 값 가져와서 변환
        LocalDate watchedDate = LocalDate.parse(today, DateTimeFormatter.ISO_LOCAL_DATE);
        ContentDailyStatisticsDto dto
                = ContentDailyStatisticsDto.builder()
                .contentPostId(item.getContentPostId())
                .userId(item.getUserId())
                .date(watchedDate)
                .build();

        long start = System.currentTimeMillis();
        // log data cursor
        List<ContentPostWatchHistory> histories;
        long cursor = 0L;
        do {
            histories = qContentPostWatchHistoryRepository.findAllByContentId(item.getContentPostId(), watchedDate, cursor);

            if (!histories.isEmpty()) {
                histories.forEach(dto::merge); // merge data
                cursor = histories.getLast().getId();
            }
        } while (!histories.isEmpty() && histories.size() == batchSize);
        log.info("aggregate = {} ms", System.currentTimeMillis() - start);

        // 수입 정산
        CalculateIncomeFromViews.updateCalculatedIncomeAndTotalViews(item, dto);

        return dto;
    }

}
