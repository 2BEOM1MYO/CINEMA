package com.zb.cinema.admin.service;

import com.zb.cinema.admin.entity.Theater;
import com.zb.cinema.admin.model.InputTheater;
import com.zb.cinema.admin.repository.TheaterRepository;
import com.zb.cinema.movie.entity.Movie;
import com.zb.cinema.movie.model.ResponseMessage;
import com.zb.cinema.movie.repository.MovieRepository;
import com.zb.cinema.movie.type.ErrorCode;
import com.zb.cinema.movie.type.MovieStatus;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;

    public ResponseMessage setMovieScreeningStatus(Long movieCode, MovieStatus status) {

        Optional<Movie> optionalMovie = movieRepository.findById(movieCode);
        if (!optionalMovie.isPresent()) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        Movie movie = optionalMovie.get();
        if (movie.getStatus() == status) {
            if (status == MovieStatus.STATUS_OVER) {
                return ResponseMessage.fail(ErrorCode.MOVIE_ALREADY_NOT_SHOWING.getDescription());
            } else if (status == MovieStatus.STATUS_SHOWING) {
                return ResponseMessage.fail(ErrorCode.MOVIE_ALREADY_SHOWING.getDescription());
            } else if (status == MovieStatus.STATUS_WILL) {
                return ResponseMessage.fail(ErrorCode.MOVIE_ALREADY_WILL_SHOWING.getDescription());
            }
        }

        movie.setStatus(status);
        movieRepository.save(movie);

        return ResponseMessage.success(movie);
    }

    public ResponseMessage getMovieListByStatus(MovieStatus status) {
        List<Movie> movieList = movieRepository.findAllByStatus(status);
        if (movieList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }
        return ResponseMessage.success(movieList);
    }

    public ResponseMessage registerTheater(InputTheater inputTheater) {
        String area = inputTheater.getArea();
        String city = inputTheater.getCity();
        String name = inputTheater.getName();

        if (theaterRepository.countByAreaAndCityAndName(area, city, name) > 0) {
            return ResponseMessage.fail("이미 존재하는 극장입니다.");
        }

        Theater theater = Theater.builder()
            .area(area)
            .city(city)
            .name(name)
            .build();

        theaterRepository.save(theater);
        return ResponseMessage.success(theater);
    }
}
