package com.zb.cinema.movie.controller;

import com.zb.cinema.movie.entity.MovieCode;
import com.zb.cinema.movie.entity.MovieInfo;
import com.zb.cinema.movie.component.KobisManager;
import com.zb.cinema.movie.model.InputDate;
import com.zb.cinema.movie.model.InputDates;
import com.zb.cinema.movie.model.InputMovieCode;
import com.zb.cinema.movie.model.InputMovieNm;
import com.zb.cinema.movie.model.ResponseMessage;
import com.zb.cinema.movie.repository.MovieCodeRepository;
import com.zb.cinema.movie.repository.MovieInfoRepository;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private final KobisManager kobisManager;
    private final MovieCodeRepository movieCodeRepository;
    private final MovieInfoRepository movieInfoRePository;

    @PostMapping("/api/movie/register/movieCode") //yyyyMMdd
    public ResponseEntity<?> fetchMovieCode(@RequestBody InputDate date) {

        List<MovieCode> movieCodeList;
        try {
            movieCodeList = kobisManager.fetchBoxOfficeResult(date.getDate());
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseMessage.fail("입력값이 정확하지 않습니다."),
                HttpStatus.BAD_REQUEST);
        }
        if (movieCodeList.size() < 1) {
            return new ResponseEntity<>(ResponseMessage.fail("조회된 영화가 없습니다."),
                HttpStatus.BAD_REQUEST);
        }
        movieCodeRepository.saveAll(movieCodeList);

        return ResponseEntity.ok().body(ResponseMessage.success(movieCodeList));
    }

    @PostMapping("/api/movie/register/movieCodes") //yyyyMMdd
    public ResponseEntity<?> fetchManyMovieCodes(@RequestBody InputDates dates)
        throws ParseException, UnsupportedEncodingException, org.json.simple.parser.ParseException {

        Set<MovieCode> movieCodeList;
        try {
            movieCodeList = kobisManager.fetchManyBoxOfficeResult(dates.getStartDt(),
                dates.getEndDt());
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseMessage.fail(""), HttpStatus.BAD_REQUEST);
        }
        if (movieCodeList.size() < 1) {
            return new ResponseEntity<>(ResponseMessage.fail("조회된 영화가 없습니다."),
                HttpStatus.BAD_REQUEST);
        }

        movieCodeRepository.saveAll(movieCodeList);

        return ResponseEntity.ok().body(ResponseMessage.success(movieCodeList));
    }

    @PostMapping("/api/movie/register/movieInfo/movieCode") //yyyyMMdd
    public ResponseEntity<?> fetchMovieInfoByMovieCode(@RequestBody InputMovieCode inputMovieCode)
        throws UnsupportedEncodingException, ParseException, org.json.simple.parser.ParseException {

        MovieInfo movie = kobisManager.fetchMovieInfoResult(inputMovieCode.getMovieCode());
        if (movie == null) {
            return new ResponseEntity<>(ResponseMessage.fail("영화가 조회되지 않았습니다."),
                HttpStatus.BAD_REQUEST);
        }
        movieInfoRePository.save(movie);

        return ResponseEntity.ok().body(ResponseMessage.success(movie));
    }

    @PostMapping("/api/movie/register/movieInfo/movieNm") //yyyyMMdd
    public ResponseEntity<?> fetchMovieInfoByMovieNm(@RequestBody InputMovieNm inputMovieNm)
        throws UnsupportedEncodingException, ParseException, org.json.simple.parser.ParseException {

        List<MovieCode> movieCodeList = movieCodeRepository.findByTitleContaining(
            inputMovieNm.getMovieNm());
        List<MovieInfo> movieInfoList = new ArrayList<>();

        for (MovieCode movieCodeDto : movieCodeList) {
            Long movieCode = movieCodeDto.getCode();
            MovieInfo movie = kobisManager.fetchMovieInfoResult(movieCode);
            if (movie == null) {
                return new ResponseEntity<>(ResponseMessage.fail("영화가 조회되지 않았습니다."),
                    HttpStatus.BAD_REQUEST);
            }
            movieInfoList.add(movie);
        }
        if (movieInfoList.size() < 1) {
            return ResponseEntity.ok().body(ResponseMessage.fail("해당 이름의 영화가 없습니다."));
        }

        movieInfoRePository.saveAll(movieInfoList);

        return ResponseEntity.ok().body(ResponseMessage.success(movieInfoList));
    }

    @GetMapping("api/movie/code/{movieNm}")
    public ResponseEntity<?> getMovieCodeByTitle(@PathVariable String movieNm) {
        List<MovieCode> movieCodeList = movieCodeRepository.findByTitleContaining(movieNm);

        if (movieCodeList.size() < 1) {
            return new ResponseEntity<>(ResponseMessage.fail("영화가 조회되지 않았습니다."),
                HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(ResponseMessage.success(movieCodeList));
    }

    @GetMapping("api/movie/info/title/{movieNm}")
    public ResponseEntity<?> movieInfoListByTitle(@PathVariable String movieNm) {
        List<MovieInfo> movieInfoList = movieInfoRePository.findAllByTitleContaining(movieNm);

        if (movieInfoList.size() < 1) {
            return new ResponseEntity<>(ResponseMessage.fail("영화가 조회되지 않았습니다."),
                HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().body(ResponseMessage.success(movieInfoList));
    }

    @DeleteMapping("api/movie/delete/{movieNm}")
    public ResponseEntity<?> deleteMovieInfo(@PathVariable String movieNm) {
        List<MovieInfo> movieInfoList = movieInfoRePository.findAllByTitleContaining(movieNm);
        if (movieInfoList.size() > 1) {
            return ResponseEntity.ok()
                .body(ResponseMessage.fail("여러개의 영화가 조회되었습니다. 조금 더 정확한 이름을 입력해주세요."));
        } else if (movieInfoList.size() < 1) {
            return ResponseEntity.ok().body(ResponseMessage.fail("영화가 조회되지 않았습니다."));
        }

        movieInfoRePository.delete(movieInfoList.get(0));
        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    @GetMapping("api/movie/info/genre/{genre}")
    public ResponseEntity<?> movieInfoListByGenre(@PathVariable String genre) {
        List<MovieInfo> movieInfoList;

        try {
            movieInfoList = movieInfoRePository.findAllByGenreContaining(genre);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseMessage.fail("영화 조회에 실패하였습니다."),
                HttpStatus.BAD_REQUEST);
        }
        if (movieInfoList.size() < 1) {
            return ResponseEntity.ok().body(ResponseMessage.fail("영화가 조회되지 않았습니다."));
        }

        return ResponseEntity.ok().body(ResponseMessage.success(movieInfoList));
    }

    @GetMapping("api/movie/info/director/{director}")
    public ResponseEntity<?> movieInfoListByDirector(@PathVariable String director) {
        List<MovieInfo> movieInfoList;

        try {
            movieInfoList = movieInfoRePository.findAllByDirectorsContaining(director);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseMessage.fail("영화 조회에 실패하였습니다."),
                HttpStatus.BAD_REQUEST);
        }
        if (movieInfoList.size() < 1) {
            return ResponseEntity.ok().body(ResponseMessage.fail("영화가 조회되지 않았습니다."));
        }

        return ResponseEntity.ok().body(ResponseMessage.success(movieInfoList));
    }

    @GetMapping("api/movie/info/actor/{actor}")
    public ResponseEntity<?> movieInfoListByActor(@PathVariable String actor) {
        List<MovieInfo> movieInfoList;

        try {
            movieInfoList = movieInfoRePository.findAllByActorsContaining(actor);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseMessage.fail("영화 조회에 실패하였습니다."),
                HttpStatus.BAD_REQUEST);
        }
        if (movieInfoList.size() < 1) {
            return ResponseEntity.ok().body(ResponseMessage.fail("영화가 조회되지 않았습니다."));
        }

        return ResponseEntity.ok().body(ResponseMessage.success(movieInfoList));
    }
}