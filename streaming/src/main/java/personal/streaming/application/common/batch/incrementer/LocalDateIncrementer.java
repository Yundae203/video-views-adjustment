package personal.streaming.application.common.batch.incrementer;


import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class LocalDateIncrementer implements JobParametersIncrementer {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public JobParameters getNext(JobParameters parameters) {
        String today = LocalDate.now().minusDays(1).format(formatter);

        return new JobParametersBuilder(parameters)
                .addString("today", today)
                .toJobParameters();
    }
}
