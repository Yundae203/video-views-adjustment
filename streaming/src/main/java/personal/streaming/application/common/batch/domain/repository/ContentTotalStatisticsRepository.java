package personal.streaming.application.common.batch.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import personal.streaming.application.common.batch.domain.ContentTotalStatistics;


public interface ContentTotalStatisticsRepository extends JpaRepository<ContentTotalStatistics, Long> {

}
