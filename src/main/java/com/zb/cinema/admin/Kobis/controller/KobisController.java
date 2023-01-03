package com.zb.cinema.admin.Kobis.controller;

import com.zb.cinema.admin.Kobis.component.KobisManager;
import com.zb.cinema.admin.entity.MovieCode;
import com.zb.cinema.admin.repository.MovieCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class KobisController {

    private final KobisManager kobisManager;
    private final MovieCodeRepository movieCodeRepository;

    @PostMapping("/api/admin/movieCode/{date}")
    public ResponseEntity<?> fetch(@PathVariable String date) throws UnsupportedEncodingException, ParseException, org.json.simple.parser.ParseException {

        List<MovieCode> movieCodeList = kobisManager.fetch(date);
        movieCodeRepository.saveAll(kobisManager.fetch(date));

        return ResponseEntity.ok(movieCodeList);
    }

    @PostMapping("/api/admin/movieCode/{start}/{end}")
    public ResponseEntity<?> fetchAll(@PathVariable String start, @PathVariable String end) throws ParseException, UnsupportedEncodingException, org.json.simple.parser.ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date startFormatDate = format.parse(start);
        Date endFormatDate = format.parse(end);
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(startFormatDate);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endFormatDate);

        while (calStart.before(calEnd)) {
            calStart.add(Calendar.DATE, 1);
            List<MovieCode> movieCodeList = kobisManager.fetch(format.format(calStart.getTime()));
            movieCodeRepository.saveAll(movieCodeList);
        }

        return ResponseEntity.ok().build();
    }
}