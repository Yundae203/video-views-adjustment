package personal.streaming.application.common.batch.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WatchHistoryStatistics {

    long contentViews;
    long adViews;
    private long playTime;

    @Builder
    public WatchHistoryStatistics(
            long contentViews,
            long adViews,
            long playTime
    ) {
        this.contentViews = contentViews;
        this.adViews = adViews;
        this.playTime = playTime;
    }

    public void merge(final WatchHistoryStatistics other) {
        this.contentViews += other.contentViews;
        this.adViews += other.adViews;
        this.playTime += other.playTime;
    }
}
