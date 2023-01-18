package com.zb.cinema.movie.service;

import com.zb.cinema.movie.component.KobisManager;
import com.zb.cinema.movie.entity.Movie;
import com.zb.cinema.movie.entity.MovieCode;
import com.zb.cinema.movie.model.request.kobis.boxOffice.BoxOffice;
import com.zb.cinema.movie.model.request.kobis.boxOffice.BoxOfficeResultList;
import com.zb.cinema.movie.model.request.kobis.movieInfo.Actors;
import com.zb.cinema.movie.model.request.kobis.movieInfo.Directors;
import com.zb.cinema.movie.model.request.kobis.movieInfo.Genres;
import com.zb.cinema.movie.model.request.kobis.movieInfo.MovieInfo;
import com.zb.cinema.movie.model.request.kobis.movieInfo.MovieInfoOutput;
import com.zb.cinema.movie.model.request.kobis.movieInfo.Nations;
import com.zb.cinema.movie.model.request.InputDates;
import com.zb.cinema.movie.model.response.ResponseMessage;
import com.zb.cinema.movie.repository.MovieCodeRepository;
import com.zb.cinema.movie.repository.MovieRepository;
import com.zb.cinema.movie.type.ErrorCode;
import com.zb.cinema.movie.type.MovieStatus;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieCodeRepository movieCodeRepository;
    private final MovieRepository movieRePository;

    // --------------------test
    private final KobisManager kobisManager;

    private String arrangeStr(String str) {
        if (str == "") {
            return "";
        }
        return str.substring(0, str.length() - 2);
    }

    public ResponseMessage saveMovieCode(String date) {
        BoxOffice boxOffice = kobisManager.fetchBoxOfficeResult(date);
        List<MovieCode> movieCodeList = new ArrayList<>();
        for (BoxOfficeResultList item : boxOffice.getBoxOfficeResult().getDailyBoxOfficeList()) {
            movieCodeList.add(
                MovieCode.builder()
                    .code(Long.parseLong(item.getMovieCd()))
                    .title(item.getMovieNm())
                    .build()
            );
        }

        movieCodeRepository.saveAll(movieCodeList);

        return ResponseMessage.success(movieCodeList);
    }

    public ResponseMessage saveManyMovieCodes(InputDates dates) {
        Set<MovieCode> movieCodeList = new HashSet<>();
        List<BoxOfficeResultList> boxOfficeList = new ArrayList<>();
        try {
            boxOfficeList = kobisManager.fetchManyBoxOfficeResult(
                dates.getStartDt(), dates.getEndDt());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (BoxOfficeResultList item : boxOfficeList) {
            movieCodeList.add(
                MovieCode.builder()
                    .code(Long.parseLong(item.getMovieCd()))
                    .title(item.getMovieNm())
                    .build()
            );
        }

        movieCodeRepository.saveAll(movieCodeList);

        return ResponseMessage.success(movieCodeList);
    }

    public ResponseMessage saveMovieInfoByMovieCode(Long movieCode) throws ParseException {

        MovieInfoOutput movieInfoOutput = kobisManager.fetchMovieInfoResult(movieCode);

        MovieInfo movieInfo = movieInfoOutput.getMovieInfoResult().getMovieInfo();
        List<Directors> directors = movieInfo.getDirectors();
        String director = "";
        for (Directors item : directors) {
            director += item.getPeopleNm();
            director += ", ";
        }

        List<Actors> actors = movieInfo.getActors();
        String actor = "";
        int cnt = 0;
        for (Actors item : actors) {
            cnt++;
            actor += item.getPeopleNm();
            actor += ", ";
            if (cnt > 10) {
                break;
            }
        }

        List<Genres> genres = movieInfo.getGenres();
        String genre = "";
        for (Genres item : genres) {
            genre += item.getGenreNm();
            genre += ", ";
        }

        List<Nations> nations = movieInfo.getNations();
        String nation = "";
        for (Nations item : nations) {
            nation += item.getNationNm();
            nation += ", ";
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        LocalDateTime openDt = null;
        if (movieInfo.getOpenDt() != "") {
            openDt = format.parse((String) movieInfo.getOpenDt())
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        }

        Movie movie = Movie.builder()
            .code(Long.parseLong(
                movieInfo.getMovieCd()))
            .title(movieInfo.getMovieNm())
            .actors(arrangeStr(actor))
            .directors(arrangeStr(director))
            .genre(arrangeStr(genre))
            .nation(arrangeStr(nation))
            .runTime(Long.parseLong(movieInfo.getShowTm()))
            .openDt(openDt)
            .status(MovieStatus.STATUS_WILL)
            .build();

//        movie.setStatus(MovieStatus.STATUS_WILL);
        movieRePository.save(movie);
        return ResponseMessage.success(movie);
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
