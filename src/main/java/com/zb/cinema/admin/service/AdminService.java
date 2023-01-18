package com.zb.cinema.admin.service;

import com.zb.cinema.admin.entity.Auditorium;
import com.zb.cinema.admin.entity.Seat;
import com.zb.cinema.admin.entity.Theater;
import com.zb.cinema.admin.model.response.AdminMemberDto;
import com.zb.cinema.admin.model.response.AuditoriumSchedule;
import com.zb.cinema.admin.model.request.InputAuditorium;
import com.zb.cinema.admin.model.request.InputTheater;
import com.zb.cinema.admin.repository.AuditoriumRepository;
import com.zb.cinema.admin.repository.SeatRepository;
import com.zb.cinema.admin.repository.TheaterRepository;
import com.zb.cinema.config.jwt.TokenProvider;
import com.zb.cinema.member.entity.Member;
import com.zb.cinema.member.exception.MemberError;
import com.zb.cinema.member.exception.MemberException;
import com.zb.cinema.member.model.MemberDto;
import com.zb.cinema.member.repository.MemberRepository;
import com.zb.cinema.member.type.MemberType;
import com.zb.cinema.movie.entity.Movie;
import com.zb.cinema.movie.model.response.ResponseMessage;
import com.zb.cinema.movie.model.response.ResponseMessageHeader;
import com.zb.cinema.movie.repository.MovieRepository;
import com.zb.cinema.movie.type.ErrorCode;
import com.zb.cinema.movie.type.MovieStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final SeatRepository seatRepository;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    public boolean isAdmin(String token) {
        String email = "";
        email = tokenProvider.getUserPk(token);
        Member adminMember = memberRepository.findByEmail(email).get();

        if (adminMember.getType() == MemberType.ROLE_READWRITE) {
            return false;
        }
        return true;
    }

    public ResponseMessage setMovieScreeningStatus(Long movieCode, MovieStatus status,
        String token) {

        if (!isAdmin(token)) {
            return ResponseMessage.fail(ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }

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

    public ResponseMessage registerAuditorium(InputAuditorium inputAuditorium, String token) {

        if (!isAdmin(token)) {
            return ResponseMessage.fail(ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }

        Optional<Theater> optionalTheater = theaterRepository.findById(
            inputAuditorium.getTheaterId());
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
            long theater_id = item.getTheater().getId();
            Theater theater = theaterRepository.findById(theater_id).get();
            auditoriumSchedules.add(AuditoriumSchedule.builder()
                .theaterId(theater_id)
                .auditoriumId(item.getId())
                .theaterNm(theater.getArea() + " " + theater.getCity() + " " + theater.getName())
                .movieId(item.getMovie().getCode())
                .title(item.getMovie().getTitle())
                .startDt(item.getStartDt())
                .endDt(item.getEndDt())
                .build());
        }

        return ResponseMessage.success(auditoriumSchedules);
    }

    public ResponseMessage setMemberType(String token, String memberEmail, MemberType memberType) {

        if (!isAdmin(token)) {
            return ResponseMessage.fail(ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }

        Optional<Member> optionalMember = memberRepository.findByEmail(memberEmail);
        if (!optionalMember.isPresent()) {
            return ResponseMessage.fail(MemberError.MEMBER_NOT_FOUND.getDescription());
        }
        Member member = optionalMember.get();
        if (member.getType() == memberType) {
            if (memberType == MemberType.ROLE_ADMIN) {
                return ResponseMessage.fail("이미 관리자회원 입니다.");
            } else if (memberType == MemberType.ROLE_READWRITE) {
                return ResponseMessage.fail("이미 일반 회원 입니다.");
            } else if (memberType == MemberType.ROLE_UN_ACCESSIBLE) {
                return ResponseMessage.fail("이미 정지된 회원 입니다.");
            }
        }

        member.setType(memberType);
        memberRepository.save(member);

        return ResponseMessage.success();
    }

    public ResponseMessage getAllMember(String token) {

        if (!isAdmin(token)) {
            return ResponseMessage.fail(ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }

        List<Member> memberList = memberRepository.findAll();
        List<AdminMemberDto> adminMemberDtoList = new ArrayList<>();
        for (Member member : memberList) {
            adminMemberDtoList.add(AdminMemberDto.from(member));
        }

        return ResponseMessage.success(adminMemberDtoList);
    }

    public ResponseMessage registerAdmin(String token, String email, String password, String name,
        String phone) {

        if (!isAdmin(token)) {
            return ResponseMessage.fail(ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if (optionalMember.isPresent()) {
            throw new MemberException(MemberError.MEMBER_ALREADY_EMAIL);
        }

        String pw = BCrypt.hashpw(password, BCrypt.gensalt());

        return ResponseMessage.success(MemberDto.fromEntity(memberRepository.save(
            Member.builder().email(email).password(pw).name(name).phone(phone)
                .regDt(LocalDateTime.now()).type(MemberType.ROLE_ADMIN).build())));
    }
}
