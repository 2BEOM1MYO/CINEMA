package com.zb.cinema.movie.scheduler;

import com.zb.cinema.movie.entity.MovieCode;
import com.zb.cinema.movie.service.MovieService;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MovieScheduler {

    private MovieService movieService;

    @Scheduled(cron = "0 0 12 * * *")
    public void fetchToday() throws ParseException {
        LocalDate day = LocalDate.now().minusDays(1);
        String dayString = day.toString().replace("-", "");

        List<MovieCode> movieCodeList = (List<MovieCode>) movieService.saveMovieCode(
            dayString).getBody();

        for (MovieCode item : movieCodeList) {
            System.out.println(movieService.saveMovieInfoByMovieCode(
                item.getCode()));
        }
    }
}
