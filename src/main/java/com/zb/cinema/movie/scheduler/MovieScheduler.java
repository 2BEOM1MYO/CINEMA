package com.zb.cinema.movie.scheduler;

import com.zb.cinema.movie.entity.Movie;
import com.zb.cinema.movie.entity.MovieCode;
import com.zb.cinema.movie.model.response.ResponseMessage;
import com.zb.cinema.movie.service.MovieService;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MovieScheduler {

    private MovieService movieService;

    @Scheduled(cron = "0 0 12 * * *")
    public ResponseMessage saveDayMovieInfo() throws ParseException {
        LocalDate day = LocalDate.now().minusDays(1);
        String dayString = day.toString().replace("-", "");

        List<Movie> movieList = new ArrayList<>();
        List<MovieCode> movieCodeList = movieService.saveMovieCode(
            dayString);

        for (MovieCode movieCode : movieCodeList) {
            movieList.add(
                movieService.saveMovieInfoByMovieCode(movieCode.getCode()));
        }
        return ResponseMessage.success(movieList);
    }
}
