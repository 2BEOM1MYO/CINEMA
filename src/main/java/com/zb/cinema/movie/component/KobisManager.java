package com.zb.cinema.movie.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zb.cinema.movie.model.request.kobis.boxOffice.BoxOffice;
import com.zb.cinema.movie.model.request.kobis.boxOffice.BoxOfficeResultList;
import com.zb.cinema.movie.model.request.kobis.movieInfo.MovieInfoOutput;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KobisManager {

    @Value("${kobis.baseUrl}")
    private String BASE_URL;
    @Value("${kobis.serviceKey}")
    private String serviceKey;

    private String makeBoxOfficeResultUrl(String date) throws UnsupportedEncodingException {
        return BASE_URL +
            "/boxoffice/searchDailyBoxOfficeList.json?key=" +
            serviceKey +
            "&targetDt=" +
            date;
    }

    private String makeMovieInfoResultUrl(long movieCode) {
        return BASE_URL +
            "/movie/searchMovieInfo.json?key=" +
            serviceKey +
            "&movieCd=" +
            movieCode;
    }

    public BoxOffice fetchBoxOfficeResult(String date) {

        String jsonString = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            jsonString = restTemplate.getForObject(makeBoxOfficeResultUrl(date), String.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        BoxOffice boxOffice = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            boxOffice = objectMapper.readValue(jsonString, BoxOffice.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return boxOffice;
    }

    public List<BoxOfficeResultList> fetchManyBoxOfficeResult(String startDt, String endDt)
        throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date startFormatDate = format.parse(startDt);
        Date endFormatDate = format.parse(endDt);
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(startFormatDate);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endFormatDate);

        List<BoxOfficeResultList> boxOfficeList = new ArrayList<>();

        while (calStart.before(calEnd)) {
            calStart.add(Calendar.DATE, 1);
//            System.out.println(format.format((calStart.getTime())));
            boxOfficeList.addAll(
                fetchBoxOfficeResult(format.format((calStart.getTime()))).getBoxOfficeResult()
                    .getDailyBoxOfficeList());
        }

        return boxOfficeList;
    }

    public MovieInfoOutput fetchMovieInfoResult(Long movieCode) {
        String jsonString = "";
        RestTemplate restTemplate = new RestTemplate();
        jsonString = restTemplate.getForObject(makeMovieInfoResultUrl(movieCode), String.class);

        MovieInfoOutput movieInfoOutput = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            movieInfoOutput = objectMapper.readValue(jsonString, MovieInfoOutput.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return movieInfoOutput;
    }
}
