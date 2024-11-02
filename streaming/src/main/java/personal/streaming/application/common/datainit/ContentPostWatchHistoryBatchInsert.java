package personal.streaming.application.common.datainit;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import personal.streaming.content_post_watch_history.domain.ContentPostWatchHistory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ContentPostWatchHistoryBatchInsert {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void insert(final List<ContentPostWatchHistory> watchHistories) {
        String SQL = """
                INSERT INTO content_post_watch_history (user_id, content_post_id, watched_at, ad_views, play_time, paused_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(SQL, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ContentPostWatchHistory history = watchHistories.get(i);
                ps.setLong(1, history.getUserId());
                ps.setLong(2, history.getContentPostId());
                ps.setObject(3, history.getWatchedAt()); // LocalDate는 setObject로 설정
                ps.setLong(4, history.getAdViews());
                ps.setLong(5, history.getPlayTime());
                ps.setLong(6, history.getPausedAt());
            }

            @Override
            public int getBatchSize() {
                return watchHistories.size();
            }
        });
    }
}
