package personal.streaming.application.common.batch.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import personal.streaming.application.common.batch.domain.ContentDailyStatistics;

import java.time.LocalDate;

@Getter
public class ContentTotalStatisticsDto {

    private final Long id;

    private final Long contentPostId;
    private final Long userId;

    private Long totalContentView;
    private Long totalAdView;
    private Long totalIncome;
    private Long totalContentPlayTime;

    private LocalDate lastUpdate;

    @Builder
    @QueryProjection
    public ContentTotalStatisticsDto(
            Long id,
            Long contentPostId,
            Long userId,
            Long totalContentView,
            Long totalAdView,
            Long totalIncome,
            Long totalContentPlayTime,
            LocalDate lastUpdate
    ) {
        this.id = id;
        this.contentPostId = contentPostId;
        this.userId = userId;
        this.totalContentView = totalContentView;
        this.totalAdView = totalAdView;
        this.totalIncome = totalIncome;
        this.totalContentPlayTime = totalContentPlayTime;
        this.lastUpdate = lastUpdate;
    }

    public void addTotalContentView(long totalContentView) {
        this.totalContentView += totalContentView;
    }

    public void addTotalAdView(long totalAdView) {
        this.totalAdView += totalAdView;
    }

    public void addTotalIncome(long totalIncome) {
        this.totalIncome += totalIncome;
    }
    public void addTotalContentPlayTime(long totalContentPlayTime) {
        this.totalContentPlayTime += totalContentPlayTime;
    }
    public void updateLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void rollback(ContentDailyStatistics dailyStatistics) {
        this.totalAdView -= dailyStatistics.getAdViewCount();
        this.totalContentView -= dailyStatistics.getViews();
        this.totalIncome -= dailyStatistics.getAdIncome() + dailyStatistics.getContentIncome();
        this.totalContentPlayTime -= dailyStatistics.getPlayTime();
        this.lastUpdate = null;
    }
}
