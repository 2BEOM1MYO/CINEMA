package com.zb.cinema.admin.service;

import com.zb.cinema.admin.entity.Auditorium;
import com.zb.cinema.admin.entity.Seat;
import com.zb.cinema.admin.entity.Theater;
import com.zb.cinema.admin.model.AuditoriumSchedule;
import com.zb.cinema.admin.model.InputAuditorium;
import com.zb.cinema.admin.model.InputTheater;
import com.zb.cinema.admin.repository.AuditoriumRepository;
import com.zb.cinema.admin.repository.SeatRepository;
import com.zb.cinema.admin.repository.TheaterRepository;
import com.zb.cinema.movie.entity.Movie;
import com.zb.cinema.movie.model.ResponseMessage;
import com.zb.cinema.movie.repository.MovieRepository;
import com.zb.cinema.movie.type.ErrorCode;
import com.zb.cinema.movie.type.MovieStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final SeatRepository seatRepository;

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

    public ResponseMessage registerAuditorium(InputAuditorium inputAuditorium) {

        Optional<Theater> optionalTheater = theaterRepository.findById(
            inputAuditorium.getTheater_id());
        if (!optionalTheater.isPresent()) {
            return ResponseMessage.fail(ErrorCode.THEATER_NOT_FOUND.getDescription());
        }
        Theater theater = optionalTheater.get();

        Optional<Movie> optionalMovie = movieRepository.findById(inputAuditorium.getMovieCode());
        if (!optionalMovie.isPresent()) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        Movie movie = optionalMovie.get();
        if (movie.getStatus() != MovieStatus.STATUS_SHOWING) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_SHOWING.getDescription());
        }

        LocalDateTime startDt = LocalDateTime.parse(inputAuditorium.getStartDt(),
            DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        LocalDateTime endDt = startDt.plusMinutes(movie.getRunTime());

        List<Auditorium> auditoriumList = auditoriumRepository.findAllByStartDtBetweenAndTheaterOrEndDtBetweenAndTheater(
            startDt.minusMinutes(30), endDt.plusMinutes(30), theater,
            startDt.minusMinutes(30), endDt.plusMinutes(30), theater);
        if (auditoriumList.size() > 0) {
            return ResponseMessage.fail(ErrorCode.AUDITORIUM_ALREADY_EXIST.getDescription());
        }

        List<String> seatNmList = makeSeats(inputAuditorium.getSeatNum());

        Auditorium auditorium = Auditorium.builder()
            .theater(theater)
            .movie(movie)
            .price(inputAuditorium.getPrice())
            .seatNum(inputAuditorium.getSeatNum())
            .startDt(startDt)
            .endDt(endDt)
            .build();

        List<Seat> seatList = new ArrayList<>();
        for (String seatNm : seatNmList) {
            seatList.add(Seat.builder()
                .auditorium(auditorium)
                .seatNum(seatNm)
                .isUsing(false)
                .build());
        }

        auditoriumRepository.save(auditorium);
        seatRepository.saveAll(seatList);

        return ResponseMessage.success(auditorium);
    }

    public List<String> makeSeats(long capacity) {
        List<String> seatList = new ArrayList<>();

        long rowSize = capacity / 10;
        long restSize = capacity % 10;
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (; i < rowSize; i++) {
            sb.setLength(0);
            sb.append(Character.toString('A' + i));
            for (int j = 1; j <= 10; j++) {
                sb.setLength(1);
                sb.append(j);
                seatList.add(sb.toString());
            }
        }
        sb.setLength(0);
        sb.append(Character.toString('A' + i));
        for (int j = 1; j <= restSize; j++) {
            sb.setLength(1);
            sb.append(j);
            seatList.add(sb.toString());
        }

        return seatList;
    }

    public ResponseMessage getAuditoriumByMovie(Long movieCode) {
        Optional<Movie> optionalMovie = movieRepository.findById(movieCode);
        if (!optionalMovie.isPresent()) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        List<Auditorium> auditoriumList = auditoriumRepository.findAllByMovie(optionalMovie.get());
        if (auditoriumList.size() < 1) {
            return ResponseMessage.fail("해당 영화는 상영 일정이 없습니다.");
        }

        List<AuditoriumSchedule> auditoriumSchedules = new ArrayList<>();
        for (Auditorium item : auditoriumList) {
            auditoriumSchedules.add(AuditoriumSchedule.builder()
                .theater_id(item.getTheater().getId())
                .auditorium_id(item.getId())
                .movie_id(item.getMovie().getCode())
                .title(item.getMovie().getTitle())
                .startDt(item.getStartDt())
                .endDt(item.getEndDt())
                .build());
        }

        return ResponseMessage.success(auditoriumSchedules);
    }
}
