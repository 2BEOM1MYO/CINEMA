package com.zb.cinema.movie.scheduler;

import com.zb.cinema.movie.job.BoxOfficeJobConfig;
import com.zb.cinema.movie.job.MovieInfoJobConfig;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class MovieScheduler {
    private final JobLauncher jobLauncher;
    private final BoxOfficeJobConfig boxOfficeJobConfig;
    private final MovieInfoJobConfig movieInfoJobConfig;

    @Scheduled(cron ="0 0 0 * * *") //매일 12시에 실행
    public void executeDailyJob () {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("datetime", LocalDateTime.now().toString())
                .toJobParameters();

            jobLauncher.run(
                boxOfficeJobConfig.dailyBoxOfficeJob(),
                jobParameters  // job parameter 설정
            );
        } catch (JobExecutionException ex) {
            log.info(ex.getMessage());
            ex.printStackTrace();
        }

    }

    @Scheduled(cron ="0 0 1 * * *") //매일 1시에 실행
    public void saveDayMovieInfo() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("datetime", LocalDateTime.now().toString())
                .toJobParameters();

            jobLauncher.run(
                movieInfoJobConfig.movieInfoJob(),
                jobParameters  // job parameter 설정
            );
        } catch (JobExecutionException ex) {
            log.info(ex.getMessage());
            ex.printStackTrace();
        }

    }
}
