package personal.streaming.application.common.batch.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentDailyStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contentPostId;
    private Long userId;

    private LocalDate date;
    private Long views;
    private Long contentIncome;
    private Long adIncome;
    private Long playTime;
    private Long adViewCount;

    @Builder
    public ContentDailyStatistics(
            Long id,
            Long contentPostId,
            Long userId,
            LocalDate date,
            Long views,
            Long contentIncome,
            Long adIncome,
            Long playTime,
            Long adViewCount
    ) {
        this.id = id;
        this.contentPostId = contentPostId;
        this.userId = userId;
        this.date = date;
        this.views = views;
        this.contentIncome = contentIncome;
        this.adIncome = adIncome;
        this.playTime = playTime;
        this.adViewCount = adViewCount;
    }
}
