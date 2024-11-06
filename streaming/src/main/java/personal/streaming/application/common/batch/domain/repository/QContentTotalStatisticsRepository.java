package personal.streaming.application.common.batch.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import personal.streaming.application.common.batch.dto.ContentTotalStatisticsDto;
import personal.streaming.application.common.batch.dto.QContentTotalStatisticsDto;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static personal.streaming.application.common.batch.domain.QContentTotalStatistics.*;

@Repository
public class QContentTotalStatisticsRepository {

    private final JPAQueryFactory queryFactory;

    @Value("${spring.chunk.size}")
    private int chunkSize;


    public QContentTotalStatisticsRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<ContentTotalStatisticsDto> findAllById(Collection<Long> ids) {
        return queryFactory
                .select(new QContentTotalStatisticsDto(
                        contentTotalStatistics.id,
                        contentTotalStatistics.contentPostId,
                        contentTotalStatistics.userId,
                        contentTotalStatistics.totalContentView,
                        contentTotalStatistics.totalAdView,
                        contentTotalStatistics.totalIncome,
                        contentTotalStatistics.totalContentPlayTime,
                        contentTotalStatistics.lastUpdate
                ))
                .from(contentTotalStatistics)
                .where(contentTotalStatistics.contentPostId.in(ids))
                .fetch();
    }

    public List<ContentTotalStatisticsDto> cursorByDate(LocalDate date, long id) {
        return queryFactory
                .select(new QContentTotalStatisticsDto(
                        contentTotalStatistics.id,
                        contentTotalStatistics.contentPostId,
                        contentTotalStatistics.userId,
                        contentTotalStatistics.totalContentView,
                        contentTotalStatistics.totalAdView,
                        contentTotalStatistics.totalIncome,
                        contentTotalStatistics.totalContentPlayTime,
                        contentTotalStatistics.lastUpdate
                ))
                .from(contentTotalStatistics)
                .where(
                        contentTotalStatistics.lastUpdate.eq(date),
                        contentTotalStatistics.contentPostId.gt(id)
                )
                .offset(0)
                .limit(chunkSize)
                .fetch();
    }
}
