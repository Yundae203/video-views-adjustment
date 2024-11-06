package personal.streaming.application.common.batch.domain.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import personal.streaming.application.common.batch.domain.ContentTotalStatistics;
import personal.streaming.application.common.batch.dto.ContentTotalStatisticsDto;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentTotalStatisticsBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void insert(final List<ContentTotalStatistics> contentTotalStatistics) {
        String SQL = """
                INSERT INTO content_total_statistics (
                                      content_post_id, 
                                      user_id, 
                                      total_content_view, 
                                      total_ad_view, 
                                      total_income, 
                                      total_content_play_time,
                                      last_update
                      )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ContentTotalStatistics stats = contentTotalStatistics.get(i);
                ps.setLong(1, stats.getContentPostId());
                ps.setLong(2, stats.getUserId());
                ps.setLong(3, stats.getTotalContentView());
                ps.setLong(4, stats.getTotalAdView());
                ps.setLong(5, stats.getTotalIncome());
                ps.setLong(6, stats.getTotalContentPlayTime());
                if (stats.getLastUpdate() != null) {
                    ps.setDate(7, Date.valueOf(stats.getLastUpdate()));
                } else {
                    ps.setNull(7, Types.DATE);
                }
            }

            @Override
            public int getBatchSize() {
                return contentTotalStatistics.size();
            }
        });
    }

    @Transactional
    public void update(final List<ContentTotalStatisticsDto> contentTotalStatisticsDto) {
        String SQL = """
            UPDATE content_total_statistics
            SET 
                total_content_view = ?, 
                total_ad_view = ?, 
                total_income = ?, 
                total_content_play_time = ?,
                last_update = ?
            WHERE 
                id = ?
            """;

        jdbcTemplate.batchUpdate(SQL, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ContentTotalStatisticsDto stats = contentTotalStatisticsDto.get(i);
                ps.setLong(1, stats.getTotalContentView());
                ps.setLong(2, stats.getTotalAdView());
                ps.setLong(3, stats.getTotalIncome());
                ps.setLong(4, stats.getTotalContentPlayTime());
                if (stats.getLastUpdate() != null) {
                    ps.setDate(5, Date.valueOf(stats.getLastUpdate()));
                } else {
                    ps.setNull(5, Types.DATE);
                }
                ps.setLong(6, stats.getId());
            }

            @Override
            public int getBatchSize() {
                return contentTotalStatisticsDto.size();
            }
        });
    }
}
