package personal.streaming.application.common.batch.chunk.reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import personal.streaming.application.common.batch.dto.ContentTotalStatisticsDto;
import personal.streaming.application.common.batch.domain.repository.QContentTotalStatisticsRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class ContentIdRedisReader implements ItemReader<ContentTotalStatisticsDto> {

    private final RedisTemplate<String, String> redisTemplate;
    private final QContentTotalStatisticsRepository QcontentTotalStatisticsRepository;

    @Value("#{stepExecutionContext['start']}")
    private Long cursor;
    @Value("#{stepExecutionContext['end']}")
    private Long end;
    @Value("${spring.batch.size}")
    private int batchSize;

    private final Queue<ContentTotalStatisticsDto> readQueue = new ArrayDeque<>();

    /**
     * key = logged:time:20241026
     * value = SortedSet { 1 , 2, 5, 8, 11 } 오름차순 정렬
     */
    @Value("#{jobExecutionContext['redisKey']}")
    private String key;

    @Override
    public ContentTotalStatisticsDto read() {

        if (readQueue.isEmpty() && cursor <= end) {
            readDB();
        }

        return readQueue.poll();
    }

    private void readDB(){
        long nextCursor = cursor + batchSize;

        log.info("key = {} currentCursor = {} nextCursor = {}", key, cursor, nextCursor);
        Set<String> ids = redisTemplate.opsForZSet().range(key, cursor, nextCursor - 1);

        Set<Long> longIds = new HashSet<>();
        if (ids != null) {
            longIds = ids.stream().map(Long::valueOf).collect(Collectors.toSet());
        }

        if (!longIds.isEmpty()) { // 비어있는지 확인
            List<ContentTotalStatisticsDto> histories = QcontentTotalStatisticsRepository.findAllById(longIds);
            readQueue.addAll(histories);
        }
        log.info("id amount ={}", longIds.size());
        cursor = nextCursor;
    }
}
