package personal.streaming.content_post_watch_history.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import personal.streaming.application.common.batch.dto.HistoryAggregation;
import personal.streaming.application.common.batch.dto.QHistoryAggregation;
import personal.streaming.content_post_watch_history.domain.ContentPostWatchHistory;

import java.time.LocalDate;
import java.util.List;

import static personal.streaming.content_post_watch_history.domain.QContentPostWatchHistory.contentPostWatchHistory;

@Repository
public class QContentPostWatchHistoryRepository {

    private final JPAQueryFactory queryFactory;

    public QContentPostWatchHistoryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<ContentPostWatchHistory> findAllByContentId(long contentId, LocalDate watchedAt, long id) {
        return queryFactory
                .selectFrom(contentPostWatchHistory)
                .where(
                        contentPostWatchHistory.contentPostId.eq(contentId),
                        contentPostWatchHistory.watchedAt.eq(watchedAt),
                        contentPostWatchHistory.id.gt(id)
                )
                .offset(0)
                .limit(1000)
                .fetch();
    }

    public HistoryAggregation aggregateHistoryById(long id, LocalDate watchedAt) {
        return queryFactory
                .select(new QHistoryAggregation(
                        contentPostWatchHistory.count(),
                        contentPostWatchHistory.adViews.sum(),
                        contentPostWatchHistory.playTime.sum()
                        )
                )
                .from(contentPostWatchHistory)
                .where(
                        contentPostWatchHistory.contentPostId.eq(id),
                        contentPostWatchHistory.watchedAt.eq(watchedAt)
                )
                .fetchOne();
    }
}
