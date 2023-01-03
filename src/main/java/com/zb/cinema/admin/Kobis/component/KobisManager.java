package com.zb.cinema.admin.Kobis.component;

import com.zb.cinema.admin.entity.MovieCode;
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
    private final String apiUri = "/boxoffice/searchDailyBoxOfficeList.json";
    private final String serviceKey = "?key=48656dce18398a0341dd2159167d8440";
    private final String targetDt = "&targetDt=";

    private String makeUrl(String date) throws UnsupportedEncodingException {
        return BASE_URL +
                apiUri +
                serviceKey +
                targetDt +
                date;
    }

    public List<MovieCode> fetch(String date) throws ParseException, UnsupportedEncodingException, org.json.simple.parser.ParseException {
        RestTemplate restTemplate = new RestTemplate();
        String jsonString = restTemplate.getForObject(makeUrl(date), String.class);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
        JSONObject jsonResponse = (JSONObject) jsonObject.get("boxOfficeResult");

        JSONArray jsonItemList = (JSONArray) jsonResponse.get("dailyBoxOfficeList");

        List<MovieCode> result = new ArrayList<>();

        for (Object o : jsonItemList) {
            JSONObject item = (JSONObject) o;
            result.add(makeLocationDto(item));
        }
        return result;
    }

    private MovieCode makeLocationDto(JSONObject item) {
        return MovieCode.builder()
                .code(Long.parseLong((String) item.get("movieCd")))
                .build();
    }
}
