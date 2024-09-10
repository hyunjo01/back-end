package capstone.courseweb.random.controller;

import capstone.courseweb.random.domain.SearchForm;
import capstone.courseweb.random.dto.PlaceDto;
import capstone.courseweb.random.dto.RouteDto;
import capstone.courseweb.random.service.RouteService;
import capstone.courseweb.random.service.SearchByKeywordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PlaceSearchController {
    private final SearchByKeywordService searchService;
    private final RouteService routeService;

    @GetMapping("/search/category")
    public ResponseEntity<Map<String, Object>> searchPlaces(
            @RequestParam String region,
            @RequestParam List<String> categories) throws JsonProcessingException { //, @RequestHeader("Authorization")String token

        //jwt 토큰 검증
        /**if (!jwtAuthProvider.validateToken(token.substring(7))) { //Bearer<토큰값>으로 전송되기 때문에 7번째 위치부터(토큰값만 추출)
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token"); //HTTP 401 Unauthorized 상태 코드를 반환
         }**/

        log.info("Region: " + region);
        log.info("Categories: " + categories);

        SearchForm searchForm = new SearchForm(region, categories.toArray(new String[0]));
        List<PlaceDto> placeList = new ArrayList<>();

        log.info(searchForm.getLocal());
        for (String category : searchForm.getCategories()) {
            log.info("Category: " + category);
        }

        for (int i = 0; i < searchForm.getCategories().length; i++) {
            String query = (i == 0) ? region + searchForm.getCategories()[i] : searchForm.getCategories()[i];
            String x = (i == 0) ? null : placeList.get(i-1).getX();
            String y = (i == 0) ? null : placeList.get(i-1).getY();
            boolean isFirst = (i == 0);

            placeList.add(searchService.getRandomPlace(
                    searchService.searchPlacesByKeyword(query, x, y, isFirst)));
        }

        log.info("카카오맵까진 ok");
        log.info(placeList.get(0).getPlaceName());
        log.info(placeList.get(0).getY());
        log.info(placeList.get(1).getPlaceName());
        log.info(placeList.get(1).getY());
        log.info(placeList.get(2).getPlaceName());
        log.info(placeList.get(2).getY());



        List<RouteDto> routes = routeService.findRoutesBetweenPlaces(placeList);

        List<List<String>> placeInfo = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> longitudes = new ArrayList<>();
        List<String> latitudes = new ArrayList<>();
        List<String> placeURL = new ArrayList<>();


        for (PlaceDto place : placeList) {
            names.add(place.getPlaceName());
            longitudes.add(place.getX());
            latitudes.add(place.getY());
            placeURL.add(place.getPlaceURL());
        }

        placeInfo.add(names);
        placeInfo.add(longitudes);
        placeInfo.add(latitudes);

        System.out.println("placeInfo" + placeInfo);

        // 결과로 반환할 데이터 구조 생성
        Map<String, Object> response = new HashMap<>();
        response.put("route", routes);
        response.put("info", placeInfo);



        return ResponseEntity.ok(response);
            //return ResponseEntity.ok(routes);
        //return ResponseEntity.ok(placeList);

    }


}