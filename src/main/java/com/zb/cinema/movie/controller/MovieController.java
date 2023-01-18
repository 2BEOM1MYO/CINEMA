package com.zb.cinema.movie.controller;

import com.zb.cinema.movie.model.request.InputDates;
import com.zb.cinema.movie.model.response.ResponseMessage;
import com.zb.cinema.movie.service.MovieService;
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

    @PostMapping("/movie/register/{date}") //yyyyMMdd
    public ResponseEntity<ResponseMessage> saveMovieCode(@PathVariable String date) {
        ResponseMessage result = movieService.saveMovieCode(date);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/movie/register/dates") //yyyyMMdd
    public ResponseEntity<ResponseMessage> saveMovieCodes(@RequestBody InputDates dates) {
        ResponseMessage result = movieService.saveManyMovieCodes(dates);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/movie/register/movieInfo/{movieCode}") //yyyyMMdd
    public ResponseEntity<ResponseMessage> saveMovieInfo(@PathVariable Long movieCode)
        throws ParseException {
        ResponseMessage result = movieService.saveMovieInfoByMovieCode(movieCode);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/movie/code/{movieNm}")
    public ResponseEntity<ResponseMessage> getMovieCodeByTitle(@PathVariable String movieNm) {

        ResponseMessage result = movieService.getMovieCodeByTitle(movieNm);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/movie/info/title/{movieNm}")
    public ResponseEntity<ResponseMessage> movieInfoListByTitle(@PathVariable String movieNm) {
        ResponseMessage result = movieService.movieInfoListByTitle(movieNm);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/movie/delete/{movieNm}")
    public ResponseEntity<ResponseMessage> deleteMovieInfo(@PathVariable String movieNm) {
        ResponseMessage result = movieService.deleteMovieInfo(movieNm);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/movie/info/genre/{genre}")
    public ResponseEntity<ResponseMessage> movieInfoListByGenre(@PathVariable String genre) {
        ResponseMessage result = movieService.movieInfoListByGenre(genre);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/movie/info/director/{director}")
    public ResponseEntity<ResponseMessage> movieInfoListByDirector(@PathVariable String director) {
        ResponseMessage result = movieService.movieInfoListByDirector(director);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/movie/info/actor/{actor}")
    public ResponseEntity<ResponseMessage> movieInfoListByActor(@PathVariable String actor) {
        ResponseMessage result = movieService.movieInfoListByActor(actor);
        return ResponseEntity.ok(result);
    }
}