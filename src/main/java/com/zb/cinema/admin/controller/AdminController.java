package com.zb.cinema.admin.controller;

import com.zb.cinema.admin.model.request.InputAuditorium;
import com.zb.cinema.admin.model.request.InputSchedule;
import com.zb.cinema.admin.model.request.InputTheater;
import com.zb.cinema.admin.service.AdminService;
import com.zb.cinema.member.model.RegisterMember;
import com.zb.cinema.member.type.MemberType;
import com.zb.cinema.movie.model.response.ResponseMessage;
import com.zb.cinema.movie.type.MovieStatus;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    // 영화 상영중으로 설정
    @PatchMapping("/admin/movie/{movieCode}/showing")
    public ResponseEntity<ResponseMessage> movieSetShowing(@PathVariable Long movieCode,
        @RequestHeader("token") String token) {
        ResponseMessage result = adminService.setMovieScreeningStatus(movieCode,
            MovieStatus.STATUS_SHOWING, token);
        return ResponseEntity.ok(result);
    }

    // 영화 상영예정으로 설정
    @PatchMapping("/admin/movie/{movieCode}/showing/will")
    public ResponseEntity<ResponseMessage> movieSetShowingWill(@PathVariable Long movieCode,
        @RequestHeader("token") String token) {
        ResponseMessage result = adminService.setMovieScreeningStatus(movieCode,
            MovieStatus.STATUS_WILL, token);
        return ResponseEntity.ok(result);
    }

    // 영화 상영종료 설정
    @PatchMapping("/admin/movie/{movieCode}/showing/over")
    public ResponseEntity<ResponseMessage> movieSetShowingEnd(@PathVariable Long movieCode,
        @RequestHeader("token") String token) {
        ResponseMessage result = adminService.setMovieScreeningStatus(movieCode,
            MovieStatus.STATUS_OVER, token);
        return ResponseEntity.ok(result);
    }

    // 상영중인 영화 조회
    @GetMapping("/movie/showing")
    public ResponseEntity<ResponseMessage> getMovieListShowing() {
        ResponseMessage result = adminService.getMovieListByStatus(MovieStatus.STATUS_SHOWING);
        return ResponseEntity.ok(result);
    }

    // 상영예정 영화 조회
    @GetMapping("/movie/showing/will")
    public ResponseEntity<ResponseMessage> getMovieListShowingWill() {
        ResponseMessage result = adminService.getMovieListByStatus(MovieStatus.STATUS_WILL);
        return ResponseEntity.ok(result);
    }

    // 상영종료 영화 조회
    @GetMapping("/movie/showing/over")
    public ResponseEntity<ResponseMessage> getMovieListShowingOver() {
        ResponseMessage result = adminService.getMovieListByStatus(MovieStatus.STATUS_OVER);
        return ResponseEntity.ok(result);
    }

    // 극장 등록
    @PostMapping("/admin/register/theater")
    public ResponseEntity<ResponseMessage> registerTheater(@RequestBody InputTheater theater) {
        ResponseMessage result = adminService.registerTheater(theater);
        return ResponseEntity.ok(result);
    }

    // 상영관 등록
    @PostMapping("/admin/register/auditorium")
    public ResponseEntity<ResponseMessage> registerAuditorium(
        @RequestBody InputAuditorium inputAuditorium,
        @RequestHeader("token") String token) {
        ResponseMessage result = adminService.registerAuditorium(inputAuditorium, token);
        return ResponseEntity.ok(result);
    }

    // 일정 등록
    @PostMapping("/admin/register/schedule")
    public ResponseEntity<ResponseMessage> registerSchedule(
        @RequestBody InputSchedule inputSchedule,
        @RequestHeader("token") String token) {
        ResponseMessage result = adminService.registerSchedule(inputSchedule, token);
        return ResponseEntity.ok(result);
    }

    //좌석 가격 설정

    // 상영일정 조회
    @GetMapping("/{movieCode}/schedule")
    public ResponseEntity<ResponseMessage> auditoriumByMovie(@PathVariable Long movieCode) {
        ResponseMessage result = adminService.getScheduleByMovie(movieCode);
        return ResponseEntity.ok(result);
    }

    // 상영일정 좌석 조회
    @GetMapping("/seat/{scheduleId}")
    public ResponseEntity<ResponseMessage> auditoriumSeats(@PathVariable Long scheduleId) {
        ResponseMessage result = adminService.getAuditoriumSeats(scheduleId);
        return ResponseEntity.ok(result);
    }

    // 회원 관리자로 지정
    @PutMapping("/admin/type/admin")
    public ResponseEntity<ResponseMessage> setAdmin(@RequestParam String memberEmail,
        @RequestHeader("token") String token) {

        ResponseMessage result = adminService.setMemberType(token, memberEmail,
            MemberType.ROLE_ADMIN);
        return ResponseEntity.ok(result);
    }

    // 회원 일반회원으로 지정
    @PutMapping("/admin/type/member")
    public ResponseEntity<ResponseMessage> setMember(@RequestParam String memberEmail,
        @RequestHeader("token") String token) {
        ResponseMessage result = adminService.setMemberType(token, memberEmail,
            MemberType.ROLE_READWRITE);
        return ResponseEntity.ok(result);
    }

    // 회원 정지회원으로 지정
    @PutMapping("/admin/type/ban")
    public ResponseEntity<ResponseMessage> setBan(@RequestParam String memberEmail,
        @RequestHeader("token") String token) {
        ResponseMessage result = adminService.setMemberType(token, memberEmail,
            MemberType.ROLE_UN_ACCESSIBLE);
        return ResponseEntity.ok(result);
    }

    // 회원 전체 목록 조회
    @GetMapping("/admin/member")
    public ResponseEntity<ResponseMessage> allMember(@RequestHeader("token") String token) {
        ResponseMessage result = adminService.getAllMember(token);
        return ResponseEntity.ok(result);
    }

    // 관리자가 직접 관리자 추가
    @PostMapping("/admin/register")
    public ResponseEntity<ResponseMessage> signUpAdmin(
        @RequestBody @Valid RegisterMember.Request request,
        @RequestHeader("token") String token) {
        ResponseMessage result = adminService.registerAdmin(token, request.getEmail(),
            request.getPassword(), request.getName(),
            request.getPhone());
        return ResponseEntity.ok(result);
    }
}