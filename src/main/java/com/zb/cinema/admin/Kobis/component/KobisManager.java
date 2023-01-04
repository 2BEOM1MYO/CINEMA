package com.zb.cinema.admin.Kobis.component;

import com.zb.cinema.admin.entity.MovieCode;
import com.zb.cinema.admin.entity.MovieInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class KobisManager {

    private final String BASE_URL = "http://kobis.or.kr/kobisopenapi/webservice/rest";
    private final String serviceKey = "48656dce18398a0341dd2159167d8440";

    private String makeBoxOfficeResultUrl(String date) throws UnsupportedEncodingException {
        return BASE_URL +
                "/boxoffice/searchDailyBoxOfficeList.json?key=" +
                serviceKey +
                "&targetDt=" +
                date;
    }

    public List<MovieCode> fetchBoxOfficeResult(String date) throws ParseException, UnsupportedEncodingException, org.json.simple.parser.ParseException {
        RestTemplate restTemplate = new RestTemplate();
        String jsonString = restTemplate.getForObject(makeBoxOfficeResultUrl(date), String.class);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
        JSONObject jsonResponse = (JSONObject) jsonObject.get("boxOfficeResult");
        JSONArray jsonItemList = (JSONArray) jsonResponse.get("dailyBoxOfficeList");

        List<MovieCode> result = new ArrayList<>();

        for (Object o : jsonItemList) {
            JSONObject item = (JSONObject) o;
            result.add(makeMovieCodeDto(item));
        }
        return result;
    }

    private MovieCode makeMovieCodeDto(JSONObject item) {
        return MovieCode.builder()
                .code(Long.parseLong((String) item.get("movieCd")))
                .title((String) item.get("movieNm"))
                .build();
    }

    private String makeMovieInfoResultUrl(long movieCode) throws UnsupportedEncodingException {
        return BASE_URL +
                "/movie/searchMovieInfo.json?key=" +
                serviceKey +
                "&movieCd=" +
                movieCode;
    }

//    private MovieInfo makeMovieInfoDto(JSONObject item) {
//        return MovieInfo.builder()
//                .code(Long.parseLong((String) item.get("movieCd")))
//                .title((String) item.get("movieNm"))
//                .actors((String) item.get(""))
//                .director((String) item.get(""))
//                .genre((String) item.get(""))
//                .nation((String) item.get(""))
//                .runTime(Long.parseLong((String) item.get("")))
//                .openDt(())
//                .endDt()
//                .build();
//    }
//
//    public MovieInfo fetchMovieInfoResult(Long movieCode) throws ParseException, UnsupportedEncodingException, org.json.simple.parser.ParseException {
//        RestTemplate restTemplate = new RestTemplate();
//        String jsonString = restTemplate.getForObject(makeMovieInfoResultUrl(movieCode), String.class);
//        JSONParser jsonParser = new JSONParser();
//        JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
//        JSONObject jsonResponse = (JSONObject) jsonObject.get("movieInfoResult");
//        JSONArray jsonItemList = (JSONArray) jsonResponse.get("movieInfo");
//
//        List<MovieCode> result = new ArrayList<>();
//
//        for (Object o : jsonItemList) {
//            JSONObject item = (JSONObject) o;
//            result.add(makeMovieCodeDto(item));
//        }
//        return result;
//    }


}
