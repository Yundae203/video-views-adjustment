package personal.streaming.application.common.batch.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
public class HistoryAggregation {
    private final long contentViews;
    private final long adViews;
    private final long playtime;

    @Builder
    @QueryProjection
    public HistoryAggregation(long contentViews, long adViews, long playtime) {
        this.contentViews = contentViews;
        this.adViews = adViews;
        this.playtime = playtime;
    }
}
