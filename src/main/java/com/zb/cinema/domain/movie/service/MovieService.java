package com.zb.cinema.domain.movie.service;

import com.zb.cinema.domain.movie.component.KobisManager;
import com.zb.cinema.domain.movie.entity.Movie;
import com.zb.cinema.domain.movie.entity.MovieCode;
import com.zb.cinema.domain.movie.model.request.InputDates;
import com.zb.cinema.domain.movie.model.request.kobis.boxOffice.BoxOffice;
import com.zb.cinema.domain.movie.model.request.kobis.boxOffice.BoxOfficeResultList;
import com.zb.cinema.domain.movie.model.request.kobis.movieInfo.Actors;
import com.zb.cinema.domain.movie.model.request.kobis.movieInfo.Directors;
import com.zb.cinema.domain.movie.model.request.kobis.movieInfo.MovieInfo;
import com.zb.cinema.domain.movie.model.request.kobis.movieInfo.MovieInfoOutput;
import com.zb.cinema.domain.movie.repository.MovieCodeRepository;
import com.zb.cinema.domain.movie.repository.MovieRepository;
import com.zb.cinema.domain.movie.type.ErrorCode;
import com.zb.cinema.domain.movie.type.MovieStatus;
import com.zb.cinema.domain.movie.model.request.kobis.movieInfo.Genres;
import com.zb.cinema.domain.movie.model.request.kobis.movieInfo.Nations;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieCodeRepository movieCodeRepository;
    private final MovieRepository movieRePository;
    private final KobisManager kobisManager;

    public List<MovieCode> saveMovieCode(String date) {
        BoxOffice boxOffice = kobisManager.fetchBoxOfficeResult(date);
        List<MovieCode> movieCodeList = new ArrayList<>();
        for (BoxOfficeResultList item : boxOffice.getBoxOfficeResult()
            .getDailyBoxOfficeList()) {
            movieCodeList.add(
                MovieCode.builder()
                    .code(Long.parseLong(item.getMovieCd()))
                    .title(item.getMovieNm())
                    .build()
            );
        }

        movieCodeRepository.saveAll(movieCodeList);

        return movieCodeList;
    }

    public Set<MovieCode> saveManyMovieCodes(InputDates dates) {
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

        return movieCodeList;
    }

    //영화 상세정보 저장
    public Movie saveMovieInfoByMovieCode(Long movieCode)
        throws ParseException {

        MovieInfoOutput movieInfoOutput = kobisManager.fetchMovieInfoResult(
            movieCode);
        MovieInfo movieInfo = movieInfoOutput.getMovieInfoResult()
            .getMovieInfo();
        // 감독, 배우, 장르, 국가는 list를 합쳐서 하나의 문자열로 저장
        List<Directors> directors = movieInfo.getDirectors();
        String director = directors.stream().map(Directors::getPeopleNm)
            .collect(Collectors.joining(", "));

        List<Actors> actors = movieInfo.getActors();
        String actor = actors.stream().map(Actors::getPeopleNm).limit(10)
            .collect(Collectors.joining(", "));

        List<Genres> genres = movieInfo.getGenres();
        String genre = genres.stream().map(Genres::getGenreNm)
            .collect(Collectors.joining(", "));

        List<Nations> nations = movieInfo.getNations();
        String nation = nations.stream().map(Nations::getNationNm)
            .collect(Collectors.joining(", "));
        //날짜 포맷
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        LocalDateTime openDt = null;
        if (movieInfo.getOpenDt() != "") {
            openDt = format.parse(movieInfo.getOpenDt())
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        }

        Movie movie = Movie.builder()
            .code(Long.parseLong(
                movieInfo.getMovieCd()))
            .title(movieInfo.getMovieNm())
            .actors(actor)
            .directors(director)
            .genre(genre)
            .nation(nation)
            .runTime(Long.parseLong(movieInfo.getShowTm()))
            .openDt(openDt)
            .status(MovieStatus.STATUS_WILL)
            .build();
        movieRePository.save(movie);
        return movie;
    }

    //제목으로 코드조회
    public List<MovieCode> getMovieCodeByTitle(String movieNm) {
        List<MovieCode> movieCodeList = movieCodeRepository.findByTitleContaining(
            movieNm);

        if (movieCodeList.size() < 1) {
            throw new RuntimeException(
                ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }
        return movieCodeList;
    }

    //제목으로 영화 상세정보 조회
    public List<Movie> movieInfoListByTitle(String movieNm) {
        List<Movie> movieList = movieRePository.findAllByTitleContaining(
            movieNm);

        if (movieList.size() < 1) {
            throw new RuntimeException(
                ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        return movieList;
    }

    //영화 상세정보 삭제
    public List<Movie> deleteMovieInfo(String movieNm) {
        List<Movie> movieList = movieRePository.findAllByTitleContaining(
            movieNm);
        if (movieList.size() > 1) {
            throw new RuntimeException(
                ErrorCode.MANY_MOVIE_FOUND.getDescription());
        } else if (movieList.size() < 1) {
            throw new RuntimeException(
                ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        movieRePository.delete(movieList.get(0));
        return movieList;
    }

    //장르로 영화 조회
    public List<Movie> movieInfoListByGenre(String genre) {
        List<Movie> movieList;

        try {
            movieList = movieRePository.findAllByGenreContaining(genre);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        if (movieList.size() < 1) {
            throw new RuntimeException(
                ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        return movieList;
    }

    //감독으로 영화 조회
    public List<Movie> movieInfoListByDirector(String director) {
        List<Movie> movieList;

        try {
            movieList = movieRePository.findAllByDirectorsContaining(director);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        if (movieList.size() < 1) {
            throw new RuntimeException(
                ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        return movieList;
    }

    //배우로 영화 조회
    public List<Movie> movieInfoListByActor(String actor) {
        List<Movie> movieList;

        try {
            movieList = movieRePository.findAllByActorsContaining(actor);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        if (movieList.size() < 1) {
            throw new RuntimeException(
                ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        return movieList;
    }
}
