package personal.streaming.application.common.batch.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentTotalStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long contentPostId;
    private Long userId;

    private Long totalContentView;
    private Long totalAdView;
    private Long totalIncome;
    private Long totalContentPlayTime;

    @Builder
    public ContentTotalStatistics(
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
}
