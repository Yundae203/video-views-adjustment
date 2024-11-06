package personal.streaming.application.common.batch.chunk.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import personal.streaming.application.common.batch.dto.ContentTotalStatisticsDto;
import personal.streaming.application.common.batch.domain.repository.QContentTotalStatisticsRepository;
import personal.streaming.content_post_watch_history.domain.DailyWatchedContentLog;
import personal.streaming.content_post_watch_history.repository.QDailyWatchedContentLogRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class LoggedContentReader implements ItemReader<ContentTotalStatisticsDto>, StepExecutionListener {

    private final QContentTotalStatisticsRepository qContentTotalStatisticsRepository;
    private final QDailyWatchedContentLogRepository qDailyWatchedContentLogRepository;

    @Value("#{stepExecutionContext['start']}")
    private Long start;
    @Value("#{stepExecutionContext['end']}")
    private Long end;
    @Value("#{stepExecutionContext['cursor']}")
    private Long cursor;
    @Value("#{jobParameters['today']}")
    private String today;
    @Value("${spring.chunk.size}")
    private int chunkSize;

    private LocalDate todayDate;
    
    private final Queue<ContentTotalStatisticsDto> readQueue = new ArrayDeque<>();

    @Override
    public void beforeStep(StepExecution stepExecution) {
        todayDate = LocalDate.parse(today, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public ContentTotalStatisticsDto read() {
        if (readQueue.isEmpty() && start < end) {
            readDB();
        }

        return readQueue.poll();
    }

    private void readDB(){
        long limit;
        if (start + chunkSize - 1 <= end) {
            limit = chunkSize;
        } else {
            limit = end - start + 1; // 남은 데이터를 모두 처리
        }

        List<DailyWatchedContentLog> dailyWatchedContentLogs = qDailyWatchedContentLogRepository.cursorLogs(cursor, todayDate, limit);

        List<Long> ids = dailyWatchedContentLogs.stream()
                .map(DailyWatchedContentLog::getContentPostId).toList();

        if (!ids.isEmpty()) { // 비어있는지 확인
            List<ContentTotalStatisticsDto> histories = qContentTotalStatisticsRepository.findAllById(ids);
            readQueue.addAll(histories);
        }

        start += limit;
        cursor = dailyWatchedContentLogs.getLast().getId();
    }
}
