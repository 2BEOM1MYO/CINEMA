package com.zb.cinema.movie.controller;

import com.zb.cinema.movie.entity.Movie;
import com.zb.cinema.movie.entity.MovieCode;
import com.zb.cinema.movie.model.request.InputDates;
import com.zb.cinema.movie.model.response.ResponseMessage;
import com.zb.cinema.movie.service.MovieService;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MovieController {

    private final MovieService movieService;

    @PostMapping("/movie/register/{date}") //yyyyMMdd
    public List<MovieCode> saveMovieCode(@PathVariable String date) {

        return movieService.saveMovieCode(date);
    }

    @PostMapping("/movie/register/dates") //yyyyMMdd
    public Set<MovieCode> saveMovieCodes(@RequestBody InputDates dates) {
        return movieService.saveManyMovieCodes(dates);
    }

    @PostMapping("/movie/register/movieInfo/{movieCode}") //yyyyMMdd
    public Movie saveMovieInfo(@PathVariable Long movieCode)
        throws ParseException {

        return movieService.saveMovieInfoByMovieCode(movieCode);
    }

    @GetMapping("/movie/code/{movieNm}")
    public List<MovieCode> getMovieCodeByTitle(@PathVariable String movieNm) {

        return movieService.getMovieCodeByTitle(movieNm);
    }

    @GetMapping("/movie/info/title/{movieNm}")
    public List<Movie> movieInfoListByTitle(@PathVariable String movieNm) {

        return movieService.movieInfoListByTitle(movieNm);
    }

    @DeleteMapping("/movie/delete/{movieNm}")
    public void deleteMovieInfo(@PathVariable String movieNm) {

        movieService.deleteMovieInfo(movieNm);
    }

    @GetMapping("/movie/info/genre/{genre}")
    public List<Movie> movieInfoListByGenre(@PathVariable String genre) {

        return movieService.movieInfoListByGenre(genre);
    }

    @GetMapping("/movie/info/director/{director}")
    public List<Movie> movieInfoListByDirector(@PathVariable String director) {

        return movieService.movieInfoListByDirector(director);
    }

    @GetMapping("/movie/info/actor/{actor}")
    public List<Movie> movieInfoListByActor(@PathVariable String actor) {

        return movieService.movieInfoListByActor(actor);
    }
}