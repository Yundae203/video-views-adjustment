package personal.streaming.application.common.batch.chunk.writer;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import personal.streaming.application.common.batch.domain.repository.ContentDailyStatisticsBatchRepository;
import personal.streaming.application.common.batch.domain.repository.ContentTotalStatisticsBatchRepository;
import personal.streaming.application.common.batch.dto.ContentDailyStatisticsDto;
import personal.streaming.application.common.batch.dto.ContentTotalStatisticsDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContentBatchWriter implements ItemWriter<ContentDailyStatisticsDto> {

    private final ContentDailyStatisticsBatchRepository contentDailyStatisticsBatchRepository;
    private final ContentTotalStatisticsBatchRepository contentTotalStatisticsBatchRepository;

    @Override
    public void write(Chunk<? extends ContentDailyStatisticsDto> chunk) {
        List<ContentDailyStatisticsDto> dailyStatistics = (List<ContentDailyStatisticsDto>) chunk.getItems();

        List<ContentTotalStatisticsDto> totalStatistics = dailyStatistics.stream()
                .map(ContentDailyStatisticsDto::getContentTotalStatisticsDto)
                .toList();

        contentDailyStatisticsBatchRepository.insert(dailyStatistics);
        contentTotalStatisticsBatchRepository.update(totalStatistics);
    }
}
