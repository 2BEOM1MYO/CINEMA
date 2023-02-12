package com.zb.cinema.domain.admin.controller;

import com.zb.cinema.domain.admin.entity.Auditorium;
import com.zb.cinema.domain.admin.entity.Schedule;
import com.zb.cinema.domain.admin.entity.Theater;
import com.zb.cinema.domain.admin.model.request.InputAuditorium;
import com.zb.cinema.domain.admin.model.request.InputSchedule;
import com.zb.cinema.domain.admin.model.request.InputTheater;
import com.zb.cinema.domain.admin.model.response.AdminMemberDto;
import com.zb.cinema.domain.admin.model.response.AuditoriumSchedule;
import com.zb.cinema.domain.admin.model.response.SeatModel;
import com.zb.cinema.domain.admin.service.AdminService;
import com.zb.cinema.domain.member.model.MemberDto;
import com.zb.cinema.domain.member.model.RegisterMember;
import com.zb.cinema.domain.member.type.MemberType;
import com.zb.cinema.domain.movie.entity.Movie;
import com.zb.cinema.domain.movie.type.MovieStatus;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Admin-Api")
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    // 영화 상영중으로 설정
    @PatchMapping("/admin/movie/{movieCode}/showing")
    public void movieSetShowing(@PathVariable Long movieCode,
        @RequestHeader("token") String token) {
        adminService.setMovieScreeningStatus(movieCode,
            MovieStatus.STATUS_SHOWING, token);
    }

    // 영화 상영예정으로 설정
    @PatchMapping("/admin/movie/{movieCode}/showing/will")
    public void movieSetShowingWill(@PathVariable Long movieCode,
        @RequestHeader("token") String token) {
        adminService.setMovieScreeningStatus(movieCode,
            MovieStatus.STATUS_WILL, token);
    }

    // 영화 상영종료 설정
    @PatchMapping("/admin/movie/{movieCode}/showing/over")
    public void movieSetShowingEnd(@PathVariable Long movieCode,
        @RequestHeader("token") String token) {
        adminService.setMovieScreeningStatus(movieCode,
            MovieStatus.STATUS_OVER, token);
    }

    // 상영중인 영화 조회
    @GetMapping("/movie/showing")
    public List<Movie> getMovieListShowing() {

        return adminService.getMovieListByStatus(MovieStatus.STATUS_SHOWING);
    }

    // 상영예정 영화 조회
    @GetMapping("/movie/showing/will")
    public List<Movie> getMovieListShowingWill() {

        return adminService.getMovieListByStatus(MovieStatus.STATUS_WILL);
    }

    // 상영종료 영화 조회
    @GetMapping("/movie/showing/over")
    public List<Movie> getMovieListShowingOver() {

        return adminService.getMovieListByStatus(MovieStatus.STATUS_OVER);
    }

    // 극장 등록
    @PostMapping("/admin/register/theater")
    public Theater registerTheater(@RequestBody InputTheater theater) {

        return adminService.registerTheater(theater);
    }

    // 상영관 등록
    @PostMapping("/admin/register/auditorium")
    public Auditorium registerAuditorium(
        @RequestBody InputAuditorium inputAuditorium,
        @RequestHeader("token") String token) {

        return adminService.registerAuditorium(
            inputAuditorium, token);
    }

    // 일정 등록
    @PostMapping("/admin/register/schedule")
    public Schedule registerSchedule(
        @RequestBody InputSchedule inputSchedule,
        @RequestHeader("token") String token) {
        return adminService.registerSchedule(inputSchedule,
            token);
    }

    //좌석 가격 설정
    @PostMapping("/admin/seat/{id}/price")
    public SeatModel setSeatPrice(
        @RequestHeader("token") String token, @PathVariable Long id,
        @RequestParam Long price) {

        return adminService.setSeatPrice(token, id, price);
    }

    // 상영일정 조회
    @GetMapping("/{movieCode}/schedule")
    public List<AuditoriumSchedule> auditoriumByMovie(
        @PathVariable Long movieCode) {

        return adminService.getScheduleByMovie(movieCode);
    }

    // 상영일정 좌석 조회
    @GetMapping("/seat/{scheduleId}")
    public List<SeatModel> auditoriumSeats(
        @PathVariable Long scheduleId) {

        return adminService.getAuditoriumSeats(scheduleId);
    }

    // 회원 관리자로 지정
    @PutMapping("/admin/type/admin")
    public void setAdmin(
        @RequestParam String memberEmail,
        @RequestHeader("token") String token) {

        adminService.setMemberType(token, memberEmail, MemberType.ROLE_ADMIN);
    }

    // 회원 일반회원으로 지정
    @PutMapping("/admin/type/member")
    public void setMember(
        @RequestParam String memberEmail,
        @RequestHeader("token") String token) {
        adminService.setMemberType(token, memberEmail,
            MemberType.ROLE_READWRITE);
    }

    // 회원 정지회원으로 지정
    @PutMapping("/admin/type/ban")
    public void setBan(
        @RequestParam String memberEmail,
        @RequestHeader("token") String token) {
        adminService.setMemberType(token, memberEmail,
            MemberType.ROLE_UN_ACCESSIBLE);
    }

    // 회원 전체 목록 조회
    @GetMapping("/admin/member")
    public List<AdminMemberDto> allMember(
        @RequestHeader("token") String token) {

        return adminService.getAllMember(token);
    }

    // 관리자가 직접 관리자 추가
    @PostMapping("/admin/register")
    public MemberDto signUpAdmin(
        @RequestBody @Valid RegisterMember.Request request,
        @RequestHeader("token") String token) {

        return adminService.registerAdmin(token,
            request.getEmail(),
            request.getPassword(), request.getName(),
            request.getPhone());
    }
}