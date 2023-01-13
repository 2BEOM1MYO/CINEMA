package com.zb.cinema.movie.scheduler;

import com.zb.cinema.movie.entity.MovieCode;
import com.zb.cinema.movie.model.request.InputDate;
import com.zb.cinema.movie.model.request.InputMovieCode;
import com.zb.cinema.movie.service.MovieService;
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
    public void fetchToday() {
        LocalDate day = LocalDate.now().minusDays(1);
        String dayString = day.toString().replace("-", "");

        List<MovieCode> movieCodeList = (List<MovieCode>) movieService.fetchMovieCode(
            InputDate.builder().date(dayString).build()).getBody();

        for (MovieCode item : movieCodeList) {
            System.out.println(movieService.fetchMovieInfoByMovieCode(
                InputMovieCode.builder().movieCode(item.getCode()).build()));
        }
    }
}
