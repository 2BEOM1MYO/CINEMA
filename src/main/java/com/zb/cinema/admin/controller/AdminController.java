package com.zb.cinema.admin.controller;

import com.zb.cinema.admin.model.request.InputAuditorium;
import com.zb.cinema.admin.model.request.InputTheater;
import com.zb.cinema.admin.service.AdminService;
import com.zb.cinema.movie.model.response.ResponseMessage;
import com.zb.cinema.movie.type.MovieStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/admin/movie/{movieCode}/showing")
    public ResponseEntity<ResponseMessage> movieSetShowing(@PathVariable Long movieCode) {
        ResponseMessage result = adminService.setMovieScreeningStatus(movieCode,
            MovieStatus.STATUS_SHOWING);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/admin/movie/{movieCode}/showing/will")
    public ResponseEntity<ResponseMessage> movieSetShowingWill(@PathVariable Long movieCode) {
        ResponseMessage result = adminService.setMovieScreeningStatus(movieCode,
            MovieStatus.STATUS_WILL);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/admin/movie/{movieCode}/showing/over")
    public ResponseEntity<ResponseMessage> movieSetShowingEnd(@PathVariable Long movieCode) {
        ResponseMessage result = adminService.setMovieScreeningStatus(movieCode,
            MovieStatus.STATUS_OVER);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/movie/showing")
    public ResponseEntity<ResponseMessage> getMovieListShowing() {
        ResponseMessage result = adminService.getMovieListByStatus(MovieStatus.STATUS_SHOWING);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/movie/showing/will")
    public ResponseEntity<ResponseMessage> getMovieListShowingWill() {
        ResponseMessage result = adminService.getMovieListByStatus(MovieStatus.STATUS_WILL);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/movie/showing/over")
    public ResponseEntity<ResponseMessage> getMovieListShowingOver() {
        ResponseMessage result = adminService.getMovieListByStatus(MovieStatus.STATUS_OVER);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/admin/register/theater")
    public ResponseEntity<ResponseMessage> registerTheater(@RequestBody InputTheater theater) {
        ResponseMessage result = adminService.registerTheater(theater);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/admin/register/auditorium")
    public ResponseEntity<ResponseMessage> registerAuditorium(
        @RequestBody InputAuditorium inputAuditorium) {
        ResponseMessage result = adminService.registerAuditorium(inputAuditorium);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/{movieCode}/auditorium")
    public ResponseEntity<ResponseMessage> auditoriumByMovie(@PathVariable Long movieCode) {
        ResponseMessage result = adminService.getAuditoriumByMovie(movieCode);
        return ResponseEntity.ok(result);
    }
}