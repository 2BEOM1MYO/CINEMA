package com.zb.cinema.movie.controller;

import com.zb.cinema.movie.model.request.InputDate;
import com.zb.cinema.movie.model.request.InputDates;
import com.zb.cinema.movie.model.request.InputMovieCode;
import com.zb.cinema.movie.model.request.InputMovieNm;
import com.zb.cinema.movie.model.response.ResponseMessage;
import com.zb.cinema.movie.service.MovieService;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
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

    @PostMapping("/movie/register/movieCode") //yyyyMMdd
    public ResponseEntity<?> fetchMovieCode(@RequestBody InputDate date) {
        ResponseMessage result = movieService.fetchMovieCode(date);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/movie/register/movieCodes") //yyyyMMdd
    public ResponseEntity<?> fetchManyMovieCodes(@RequestBody InputDates dates) {
        ResponseMessage result = movieService.fetchManyMovieCodes(dates);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/movie/register/movieInfo/movieCode") //yyyyMMdd
    public ResponseEntity<?> fetchMovieInfoByMovieCode(@RequestBody InputMovieCode inputMovieCode)
        throws UnsupportedEncodingException, ParseException, org.json.simple.parser.ParseException {
        ResponseMessage result = movieService.fetchMovieInfoByMovieCode(inputMovieCode);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/movie/register/movieInfo/movieNm") //yyyyMMdd
    public ResponseEntity<?> fetchMovieInfoByMovieNm(@RequestBody InputMovieNm inputMovieNm)
        throws UnsupportedEncodingException, ParseException, org.json.simple.parser.ParseException {
        ResponseMessage result = movieService.fetchMovieInfoByMovieNm(inputMovieNm);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/movie/code/{movieNm}")
    public ResponseEntity<?> getMovieCodeByTitle(@PathVariable String movieNm) {
        ResponseMessage result = movieService.getMovieCodeByTitle(movieNm);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/movie/info/title/{movieNm}")
    public ResponseEntity<?> movieInfoListByTitle(@PathVariable String movieNm) {
        ResponseMessage result = movieService.movieInfoListByTitle(movieNm);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/movie/delete/{movieNm}")
    public ResponseEntity<?> deleteMovieInfo(@PathVariable String movieNm) {
        ResponseMessage result = movieService.deleteMovieInfo(movieNm);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/movie/info/genre/{genre}")
    public ResponseEntity<?> movieInfoListByGenre(@PathVariable String genre) {
        ResponseMessage result = movieService.movieInfoListByGenre(genre);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/movie/info/director/{director}")
    public ResponseEntity<?> movieInfoListByDirector(@PathVariable String director) {
        ResponseMessage result = movieService.movieInfoListByDirector(director);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/movie/info/actor/{actor}")
    public ResponseEntity<?> movieInfoListByActor(@PathVariable String actor) {
        ResponseMessage result = movieService.movieInfoListByActor(actor);
        return ResponseEntity.ok(result);
    }
}