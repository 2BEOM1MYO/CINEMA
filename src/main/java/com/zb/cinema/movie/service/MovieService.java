package com.zb.cinema.movie.service;

import com.zb.cinema.movie.component.KobisManager;
import com.zb.cinema.movie.entity.MovieCode;
import com.zb.cinema.movie.entity.MovieInfo;
import com.zb.cinema.movie.type.ErrorCode;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final KobisManager kobisManager;
    private final MovieCodeRepository movieCodeRepository;
    private final MovieInfoRepository movieInfoRePository;

    public ResponseMessage fetchMovieCode(InputDate date) {
        List<MovieCode> movieCodeList;
        try {
            movieCodeList = kobisManager.fetchBoxOfficeResult(date.getDate());
        } catch (Exception e) {
            return ResponseMessage.fail(ErrorCode.INVALID_INPUT.getDescription());
        }
        if (movieCodeList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }
        movieCodeRepository.saveAll(movieCodeList);

        return ResponseMessage.success(movieCodeList);
    }

    public ResponseMessage fetchManyMovieCodes(InputDates dates) {
        Set<MovieCode> movieCodeList;
        try {
            movieCodeList = kobisManager.fetchManyBoxOfficeResult(dates.getStartDt(),
                dates.getEndDt());
        } catch (Exception e) {
            return ResponseMessage.fail(ErrorCode.INVALID_INPUT.getDescription());
        }
        if (movieCodeList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        movieCodeRepository.saveAll(movieCodeList);

        return ResponseMessage.success(movieCodeList);
    }

    public ResponseMessage fetchMovieInfoByMovieCode(InputMovieCode inputMovieCode) {
        MovieInfo movie;
        try {
            movie = kobisManager.fetchMovieInfoResult(inputMovieCode.getMovieCode());
        } catch (Exception e) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        movieInfoRePository.save(movie);
        return ResponseMessage.success(movie);
    }

    public ResponseMessage fetchMovieInfoByMovieNm(InputMovieNm inputMovieNm)
        throws UnsupportedEncodingException, ParseException, org.json.simple.parser.ParseException {

        List<MovieCode> movieCodeList = movieCodeRepository.findByTitleContaining(
            inputMovieNm.getMovieNm());
        List<MovieInfo> movieInfoList = new ArrayList<>();

        for (MovieCode movieCodeDto : movieCodeList) {
            Long movieCode = movieCodeDto.getCode();
            MovieInfo movie = kobisManager.fetchMovieInfoResult(movieCode);
            if (movie == null) {
                return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
            }
            movieInfoList.add(movie);
        }
        if (movieInfoList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        movieInfoRePository.saveAll(movieInfoList);

        return ResponseMessage.success(movieInfoList);
    }

    public ResponseMessage getMovieCodeByTitle(String movieNm) {
        List<MovieCode> movieCodeList = movieCodeRepository.findByTitleContaining(movieNm);

        if (movieCodeList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }
        return ResponseMessage.success(movieCodeList);
    }

    public ResponseMessage movieInfoListByTitle(String movieNm) {
        List<MovieInfo> movieInfoList = movieInfoRePository.findAllByTitleContaining(movieNm);

        if (movieInfoList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        return ResponseMessage.success(movieInfoList);
    }

    public ResponseMessage deleteMovieInfo(String movieNm) {
        List<MovieInfo> movieInfoList = movieInfoRePository.findAllByTitleContaining(movieNm);
        if (movieInfoList.size() > 1) {
            return ResponseMessage.fail(ErrorCode.MANY_MOVIE_FOUND.getDescription(), movieInfoList);
        } else if (movieInfoList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        movieInfoRePository.delete(movieInfoList.get(0));
        return ResponseMessage.success(movieInfoList);
    }

    public ResponseMessage movieInfoListByGenre(String genre) {
        List<MovieInfo> movieInfoList;

        try {
            movieInfoList = movieInfoRePository.findAllByGenreContaining(genre);
        } catch (Exception e) {
            return ResponseMessage.fail("");
        }
        if (movieInfoList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        return ResponseMessage.success(movieInfoList);
    }

    public ResponseMessage movieInfoListByDirector(String director) {
        List<MovieInfo> movieInfoList;

        try {
            movieInfoList = movieInfoRePository.findAllByDirectorsContaining(director);
        } catch (Exception e) {
            return ResponseMessage.fail("");
        }
        if (movieInfoList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        return ResponseMessage.success(movieInfoList);
    }

    public ResponseMessage movieInfoListByActor(String actor) {
        List<MovieInfo> movieInfoList;

        try {
            movieInfoList = movieInfoRePository.findAllByActorsContaining(actor);
        } catch (Exception e) {
            return ResponseMessage.fail("");
        }
        if (movieInfoList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        return ResponseMessage.success(movieInfoList);
    }

}
