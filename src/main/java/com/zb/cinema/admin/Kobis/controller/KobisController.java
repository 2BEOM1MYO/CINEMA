package com.zb.cinema.admin.Kobis.controller;

import com.zb.cinema.admin.Kobis.component.KobisManager;
import com.zb.cinema.admin.Kobis.model.ResponseMessage;
import com.zb.cinema.admin.entity.MovieCode;
import com.zb.cinema.admin.entity.MovieInfo;
import com.zb.cinema.admin.repository.MovieCodeRepository;
import com.zb.cinema.admin.repository.MovieInfoRepository;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class KobisController {

    private final KobisManager kobisManager;
    private final MovieCodeRepository movieCodeRepository;
    private final MovieInfoRepository movieInfoRePository;

    @PostMapping("/api/admin/register/movieCode/{date}") //yyyyMMdd
    public ResponseEntity<?> fetchMovieCode(@PathVariable String date) {

        List<MovieCode> movieCodeList;
        try {
            movieCodeList = kobisManager.fetchBoxOfficeResult(date);
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

    @PostMapping("/api/admin/register/movieCode/{start}/{end}") //yyyyMMdd
    public ResponseEntity<?> fetchManyMovieCodes(@PathVariable String start,
        @PathVariable String end)
        throws ParseException, UnsupportedEncodingException, org.json.simple.parser.ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date startFormatDate = format.parse(start);
        Date endFormatDate = format.parse(end);
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(startFormatDate);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endFormatDate);

        while (calStart.before(calEnd)) {
            calStart.add(Calendar.DATE, 1);
            List<MovieCode> movieCodeList = kobisManager.fetchBoxOfficeResult(
                format.format(calStart.getTime()));
            movieCodeRepository.saveAll(movieCodeList);
        }

        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    @PostMapping("/api/admin/register/movieInfo/movieCode/{movieCode}") //yyyyMMdd
    public ResponseEntity<?> fetchMovieInfoByMovieCode(@PathVariable Long movieCode)
        throws UnsupportedEncodingException, ParseException, org.json.simple.parser.ParseException {

        MovieInfo movie = kobisManager.fetchMovieInfoResult(movieCode);
        if (movie == null) {
            return new ResponseEntity<>(ResponseMessage.fail("영화가 조회되지 않았습니다."),
                HttpStatus.BAD_REQUEST);
        }
        movieInfoRePository.save(movie);

        return ResponseEntity.ok().body(ResponseMessage.success(movie));
    }

    @PostMapping("/api/admin/register/movieInfo/movieNm/{movieNm}") //yyyyMMdd
    public ResponseEntity<?> fetchMovieInfoByMovieNm(@PathVariable String movieNm)
        throws UnsupportedEncodingException, ParseException, org.json.simple.parser.ParseException {

        List<MovieCode> movieCodeList = movieCodeRepository.findByTitleContaining(movieNm);
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

        movieInfoRePository.saveAll(movieInfoList);

        return ResponseEntity.ok().body(ResponseMessage.success(movieInfoList));
    }

    @GetMapping("api/admin/code/{movieNm}")
    public ResponseEntity<?> getMovieCodeByTitle(@PathVariable String movieNm) {
        List<MovieCode> movieCodeList = movieCodeRepository.findByTitleContaining(movieNm);

        if (movieCodeList.size() < 1) {
            return new ResponseEntity<>(ResponseMessage.fail("영화가 조회되지 않았습니다."),
                HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(ResponseMessage.success(movieCodeList));
    }

    @GetMapping("api/admin/info/title/{movieNm}")
    public ResponseEntity<?> movieInfoListByTitle(@PathVariable String movieNm) {
        List<MovieInfo> movieInfoList = movieInfoRePository.findAllByTitleContaining(movieNm);

        if (movieInfoList.size() < 1) {
            return new ResponseEntity<>(ResponseMessage.fail("영화가 조회되지 않았습니다."),
                HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().body(ResponseMessage.success(movieInfoList));
    }

    @DeleteMapping("api/admin/delete/{movieNm}")
    public ResponseEntity<?> deleteMovieInfo(@PathVariable String movieNm) {
        List<MovieInfo> movieInfoList = movieInfoRePository.findAllByTitleContaining(movieNm);
        if (movieInfoList.size() > 1) {
            return ResponseEntity.ok().body(ResponseMessage.fail("여러개의 영화가 조회되었습니다."));
        } else if (movieInfoList.size() < 1) {
            return ResponseEntity.ok().body(ResponseMessage.fail("영화가 조회되지 않았습니다."));
        }

        movieInfoRePository.delete(movieInfoList.get(0));
        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    @GetMapping("api/admin/info/genre/{genre}")
    public ResponseEntity<?> movieInfoListByGenre(@PathVariable String genre) {
        List<MovieInfo> movieInfoList = null;

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

    @GetMapping("api/admin/info/director/{director}")
    public ResponseEntity<?> movieInfoListByDirector(@PathVariable String director) {
        List<MovieInfo> movieInfoList = null;

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

    @GetMapping("api/admin/info/actor/{actor}")
    public ResponseEntity<?> movieInfoListByActor(@PathVariable String actor) {
        List<MovieInfo> movieInfoList = null;

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