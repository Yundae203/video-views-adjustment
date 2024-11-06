package personal.streaming.application.common.batch.dto;

import lombok.Builder;
import lombok.Getter;
import personal.streaming.content_post_watch_history.domain.ContentPostWatchHistory;

import java.time.LocalDate;

@Getter
public class ContentDailyStatisticsDto {

    private Long id;

    private Long contentPostId;
    private Long userId;
    private LocalDate date;

    private Long views = 0L;
    private Long adViews = 0L;
    private Long playTime = 0L;
    private Long contentIncome = 0L;
    private Long adIncome = 0L;



    @Builder
    public ContentDailyStatisticsDto(
            Long id,
            Long contentPostId,
            Long userId,
            LocalDate date
    ) {
        this.id = id;
        this.contentPostId = contentPostId;
        this.userId = userId;
        this.date = date;
    }

    public void countingViewsAndPlaytime(HistoryAggregation aggregation) {
       this.views = aggregation.getContentViews();
       this.adViews = aggregation.getAdViews();
       this.playTime = aggregation.getPlaytime();
    }

    public void merge(ContentPostWatchHistory contentPostWatchHistory) {
       this.views += 1L;
       this.adViews += contentPostWatchHistory.getAdViews();
       this.playTime += contentPostWatchHistory.getPlayTime();
    }

    public void updateAdIncome(long adDailyIncome) {
       this.adIncome += adDailyIncome;
    }

    public void updateContentIncome(long contentDailyIncome) {
       this.contentIncome += contentDailyIncome;
    }
}
