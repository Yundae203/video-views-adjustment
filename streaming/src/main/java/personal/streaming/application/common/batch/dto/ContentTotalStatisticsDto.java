package personal.streaming.application.common.batch.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ContentTotalStatisticsDto {

    private final Long id;

    private final Long contentPostId;
    private final Long userId;

    private Long totalContentView;
    private Long totalAdView;
    private Long totalIncome;
    private Long totalContentPlayTime;

    @Builder
    @QueryProjection
    public ContentTotalStatisticsDto(
            Long id,
            Long contentPostId,
            Long userId,
            Long totalContentView,
            Long totalAdView,
            Long totalIncome,
            Long totalContentPlayTime
    ) {
        this.id = id;
        this.contentPostId = contentPostId;
        this.userId = userId;
        this.totalContentView = totalContentView;
        this.totalAdView = totalAdView;
        this.totalIncome = totalIncome;
        this.totalContentPlayTime = totalContentPlayTime;
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
}
