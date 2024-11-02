package personal.streaming.application.common.batch.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ContentDailyStatisticsDto {

   private final Long contentPostId;
   private final Long userId;
   private final LocalDate date;
   private final ContentTotalStatisticsDto contentTotalStatisticsDto;

   private Long views;
   private Long adViews;
   private Long playTime;
   private Long contentIncome;
   private Long adIncome;


   public ContentDailyStatisticsDto(
           Long contentPostId,
           Long userId,
           LocalDate date,
           ContentTotalStatisticsDto contentTotalStatisticsDto
   ) {
       this.contentPostId = contentPostId;
       this.userId = userId;
       this.date = date;
       this.contentTotalStatisticsDto = contentTotalStatisticsDto;
       this.views = 0L;
       this.adViews = 0L;
       this.playTime = 0L;
       this.contentIncome = 0L;
       this.adIncome = 0L;
   }

   public void countingViewsAndPlaytime(HistoryAggregation aggregation) {
       this.views = aggregation.getContentViews();
       this.adViews = aggregation.getAdViews();
       this.playTime = aggregation.getPlaytime();
   }

    public void updateAdIncome(long adDailyIncome) {
           this.adIncome += adDailyIncome;
    }

    public void updateContentIncome(long contentDailyIncome) {
       this.contentIncome += contentDailyIncome;
    }
}
