package com.zb.cinema.admin.service;

import com.zb.cinema.admin.entity.Auditorium;
import com.zb.cinema.admin.entity.Schedule;
import com.zb.cinema.admin.entity.Seat;
import com.zb.cinema.admin.entity.Theater;
import com.zb.cinema.admin.model.request.InputSchedule;
import com.zb.cinema.admin.model.response.AdminMemberDto;
import com.zb.cinema.admin.model.response.AuditoriumSchedule;
import com.zb.cinema.admin.model.request.InputAuditorium;
import com.zb.cinema.admin.model.request.InputTheater;
import com.zb.cinema.admin.model.response.SeatModel;
import com.zb.cinema.admin.repository.AuditoriumRepository;
import com.zb.cinema.admin.repository.ScheduleRepository;
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
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final AuditoriumRepository auditoriumRepository;

    //토큰으로 관리자 권한 확인
    public boolean isAdmin(String token) {
        String email = "";
        email = tokenProvider.getUserPk(token);
        Member adminMember = memberRepository.findByEmail(email).get();

        if (adminMember.getType() == MemberType.ROLE_READWRITE) {
            return false;
        }
        return true;
    }

    //영화 상태 설정 (상영중 / 상영예정 / 상영종료)
    public ResponseMessage setMovieScreeningStatus(Long movieCode, MovieStatus status,
        String token) {

        if (!isAdmin(token)) {
            return ResponseMessage.fail(ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }

        Optional<Movie> optionalMovie = movieRepository.findById(movieCode);
        if (!optionalMovie.isPresent()) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }
        //이미 같은 상태일 때 예외처리
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
        //상영일정이 존재할 경우 상영 종료설정 불가
        if (status == MovieStatus.STATUS_OVER) {
            List<Schedule> scheduleList =
                scheduleRepository.findAllByMovieAndEndDtAfter(movie, LocalDateTime.now());
            if (scheduleList.size() > 0) {
                return ResponseMessage.fail("상영 일정이 존재하여 상영종료가 불가능합니다.");
            }
            //상영 종료 설정 시 관련 데이터 삭제
            //1. 해당 상영일정의 좌석들 삭제
            //2. 상영일정 삭제
            List<Schedule> scheduleDeleteList =
                scheduleRepository.findAllByMovie(movie);
            List<Seat> seatList = new ArrayList<>();
            for (Schedule schedule : scheduleDeleteList) {
                List<Seat> tmpSeatList = seatRepository.findAllBySchedule(schedule);
                seatList.addAll(tmpSeatList);
                seatRepository.deleteAllInBatch(seatList);
            }
            scheduleRepository.deleteAllInBatch(scheduleDeleteList);
        }

        movie.setStatus(status);
        movieRepository.save(movie);

        return ResponseMessage.success(movie);
    }

    //상영 상태에 따른 조회
    public ResponseMessage getMovieListByStatus(MovieStatus status) {
        List<Movie> movieList = movieRepository.findAllByStatus(status);
        if (movieList.size() < 1) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }
        return ResponseMessage.success(movieList);
    }

    //극장 추가
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

    //상영관 추가
    public ResponseMessage registerAuditorium(InputAuditorium inputAuditorium, String token) {
        //권한 확인
        if (!isAdmin(token)) {
            return ResponseMessage.fail(ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }
        Optional<Theater> optionalTheater = theaterRepository.findById(
            inputAuditorium.getTheaterId());
        if (!optionalTheater.isPresent()) {
            return ResponseMessage.fail(ErrorCode.THEATER_NOT_FOUND.getDescription());
        }
        Theater theater = optionalTheater.get();

        if (auditoriumRepository.findByTheaterAndAndName(theater,
            inputAuditorium.getName()).isPresent()) {
            return ResponseMessage.fail(ErrorCode.AUDITORIUM_ALREADY_EXIST.getDescription());
        }

        Auditorium auditorium = Auditorium.builder()
            .theater(theater)
            .name(inputAuditorium.getName())
            .capacity(inputAuditorium.getCapacity()).build();
        auditoriumRepository.save(auditorium);

        return ResponseMessage.success(auditorium);
    }

    //상영일정 추가
    public ResponseMessage registerSchedule(InputSchedule inputSchedule, String token) {
        //권한 확인
        if (!isAdmin(token)) {
            return ResponseMessage.fail(ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }

        //상영관 확인
        Optional<Auditorium> optionalAuditorium = auditoriumRepository.findById(
            inputSchedule.getAuditoriumId());
        if (!optionalAuditorium.isPresent()) {
            return ResponseMessage.fail(ErrorCode.AUDITORIUM_NOT_FOUND.getDescription());
        }
        Auditorium auditorium = optionalAuditorium.get();

        //영화 확인
        Optional<Movie> optionalMovie = movieRepository.findById(inputSchedule.getMovieCode());
        if (!optionalMovie.isPresent()) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }
        Movie movie = optionalMovie.get();

        if (movie.getStatus() != MovieStatus.STATUS_SHOWING) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_SHOWING.getDescription());
        }

        //시간 값 포맷
        LocalDateTime startDt = LocalDateTime.parse(inputSchedule.getStartDt(),
            DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        LocalDateTime endDt = startDt.plusMinutes(movie.getRunTime());
        //이미 존재하는 상영일정에 대해 (시작시간-30 ~ 종료시간+30) 밖으로만 등록 가능
        List<Schedule> scheduleList = scheduleRepository.findAllByStartDtBetweenAndAuditoriumOrEndDtBetweenAndAuditorium(
            startDt.minusMinutes(30), endDt.plusMinutes(30), auditorium,
            startDt.minusMinutes(30), endDt.plusMinutes(30), auditorium);
        if (scheduleList.size() > 0) {
            return ResponseMessage.fail(ErrorCode.SCHEDULE_ALREADY_EXIST.getDescription());
        }

        Schedule schedule = Schedule.builder()
            .auditorium(auditorium)
            .movie(movie)
            .startDt(startDt)
            .endDt(endDt)
            .build();

        //상영일정의 좌석 설정
        List<String> seatNmList = makeSeats(auditorium.getCapacity());

        List<Seat> seatList = new ArrayList<>();
        for (String seatNm : seatNmList) {
            seatList.add(Seat.builder()
                .auditorium(auditorium)
                .schedule(schedule)
                .seatNum(seatNm)
                .isUsing(false)
                .price(13000)
                .build());
        }

        scheduleRepository.save(schedule);

        seatRepository.saveAll(seatList);

        return ResponseMessage.success(schedule);
    }

    //좌석 생성
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

    public ResponseMessage setSeatPrice(String token, Long id, Long price) {
        if (!isAdmin(token)) {
            return ResponseMessage.fail(ErrorCode.INVALID_ACCESS_MEMBER.getDescription());
        }

        Optional<Seat> optionalSeat = seatRepository.findById(id);
        if (!optionalSeat.isPresent()) {
            return ResponseMessage.fail("좌석정보가 없습니다.");
        }
        Seat seat = optionalSeat.get();
        seat.setPrice(price);
        seatRepository.save(seat);
        return ResponseMessage.success(
            SeatModel.builder()
                .id(seat.getId())
                .seatNum(seat.getSeatNum())
                .price(seat.getPrice())
                .isUsing(seat.isUsing()).build());
    }

    //    영화로 상영일정 조회
    public ResponseMessage getScheduleByMovie(Long movieCode) {
        Optional<Movie> optionalMovie = movieRepository.findById(movieCode);
        if (!optionalMovie.isPresent()) {
            return ResponseMessage.fail(ErrorCode.MOVIE_NOT_FOUND.getDescription());
        }

        List<Schedule> scheduleList = scheduleRepository.findAllByMovie(optionalMovie.get());
        if (scheduleList.size() < 1) {
            return ResponseMessage.fail("해당 영화는 상영 일정이 없습니다.");
        }

        List<AuditoriumSchedule> auditoriumSchedules = new ArrayList<>();
        for (Schedule item : scheduleList) {
            long theaterId = item.getAuditorium().getTheater().getId();
            Theater theater = theaterRepository.findById(theaterId).get();
            auditoriumSchedules.add(AuditoriumSchedule.builder()
                .theaterId(theaterId)
                .auditoriumNm(item.getAuditorium().getName())
                .theaterNm(theater.getArea() + " " + theater.getCity() + " " + theater.getName())
                .movieId(item.getMovie().getCode())
                .title(item.getMovie().getTitle())
                .startDt(item.getStartDt())
                .endDt(item.getEndDt())
                .build());
        }

        return ResponseMessage.success(auditoriumSchedules);
    }

    // 상영일정의 좌석 조회
    public ResponseMessage getAuditoriumSeats(Long scheduleId) {

        Optional<Schedule> optionalAuditorium = scheduleRepository.findById(scheduleId);
        if (!optionalAuditorium.isPresent()) {
            return ResponseMessage.fail(ErrorCode.SCHEDULE_NOT_FOUND.getDescription());
        }
        Schedule schedule = optionalAuditorium.get();
        List<Seat> seats = seatRepository.findAllBySchedule(schedule);
        List<SeatModel> seatModels = new ArrayList<>();
        for (Seat seat : seats) {
            seatModels.add(SeatModel.builder()
                .id(seat.getId())
                .seatNum(seat.getSeatNum())
                .price(seat.getPrice())
                .isUsing(seat.isUsing())
                .build());
        }

        return ResponseMessage.success(seatModels);
    }

    //회원 권한 지정
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

    //모든 회원 조회
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

    //관리자 추가
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
