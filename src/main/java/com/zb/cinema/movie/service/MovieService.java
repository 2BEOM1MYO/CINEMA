package com.zb.cinema.movie.service;

import com.zb.cinema.movie.component.KobisManager;
import com.zb.cinema.movie.entity.MovieCode;
import com.zb.cinema.movie.entity.Movie;
import com.zb.cinema.movie.type.ErrorCode;
import com.zb.cinema.movie.model.InputDate;
import com.zb.cinema.movie.model.InputDates;
import com.zb.cinema.movie.model.InputMovieCode;
import com.zb.cinema.movie.model.InputMovieNm;
import com.zb.cinema.movie.model.ResponseMessage;
import com.zb.cinema.movie.repository.MovieCodeRepository;
import com.zb.cinema.movie.repository.MovieRepository;
import com.zb.cinema.movie.type.MovieStatus;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final KobisManager kobisManager;
    private final MovieCodeRepository movieCodeRepository;
    private final MovieRepository movieRePository;

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

    @Scheduled(cron = "0 0 12 * * *")
    public void fetchToday() {
        LocalDate day = LocalDate.now().minusDays(1);
        String dayString = day.toString().replace("-", "");

        List<MovieCode> movieCodeList = (List<MovieCode>) fetchMovieCode(
            InputDate.builder().date(dayString).build()).getBody();

        for (MovieCode item : movieCodeList) {
            System.out.println(fetchMovieInfoByMovieCode(InputMovieCode.builder().movieCode(item.getCode()).build()));
        }
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
        Movie movie;
        try {
            movie = kobisManager.fetchMovieInfoResult(inputMovieCode.getMovieCode());
        } catch (Exception e) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        movie.setStatus(MovieStatus.STATUS_WILL);
        movieRePository.save(movie);
        return ResponseMessage.success(movie);
    }

    public ResponseMessage fetchMovieInfoByMovieNm(InputMovieNm inputMovieNm)
        throws UnsupportedEncodingException, ParseException, org.json.simple.parser.ParseException {

        List<MovieCode> movieCodeList = movieCodeRepository.findByTitleContaining(
            inputMovieNm.getMovieNm());
        List<Movie> movieList = new ArrayList<>();

        for (MovieCode movieCodeDto : movieCodeList) {
            Long movieCode = movieCodeDto.getCode();
            Movie movie = kobisManager.fetchMovieInfoResult(movieCode);
            if (movie == null) {
                return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
            }
            movie.setStatus(MovieStatus.STATUS_WILL);
            movieList.add(movie);
        }
        if (movieList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        movieRePository.saveAll(movieList);

        return ResponseMessage.success(movieList);
    }

    public ResponseMessage getMovieCodeByTitle(String movieNm) {
        List<MovieCode> movieCodeList = movieCodeRepository.findByTitleContaining(movieNm);

        if (movieCodeList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }
        return ResponseMessage.success(movieCodeList);
    }

    public ResponseMessage movieInfoListByTitle(String movieNm) {
        List<Movie> movieList = movieRePository.findAllByTitleContaining(movieNm);

        if (movieList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        return ResponseMessage.success(movieList);
    }

    public ResponseMessage deleteMovieInfo(String movieNm) {
        List<Movie> movieList = movieRePository.findAllByTitleContaining(movieNm);
        if (movieList.size() > 1) {
            return ResponseMessage.fail(ErrorCode.MANY_MOVIE_FOUND.getDescription(), movieList);
        } else if (movieList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        movieRePository.delete(movieList.get(0));
        return ResponseMessage.success(movieList);
    }

    public ResponseMessage movieInfoListByGenre(String genre) {
        List<Movie> movieList;

        try {
            movieList = movieRePository.findAllByGenreContaining(genre);
        } catch (Exception e) {
            return ResponseMessage.fail("");
        }
        if (movieList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        return ResponseMessage.success(movieList);
    }

    public ResponseMessage movieInfoListByDirector(String director) {
        List<Movie> movieList;

        try {
            movieList = movieRePository.findAllByDirectorsContaining(director);
        } catch (Exception e) {
            return ResponseMessage.fail("");
        }
        if (movieList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        return ResponseMessage.success(movieList);
    }

    public ResponseMessage movieInfoListByActor(String actor) {
        List<Movie> movieList;

        try {
            movieList = movieRePository.findAllByActorsContaining(actor);
        } catch (Exception e) {
            return ResponseMessage.fail("");
        }
        if (movieList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        return ResponseMessage.success(movieList);
    }

}
