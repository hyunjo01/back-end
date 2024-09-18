package capstone.courseweb.random.service;

import capstone.courseweb.random.domain.SearchForm;
import capstone.courseweb.random.domain.SelectedCategory;
import capstone.courseweb.random.dto.PlaceDto;
import capstone.courseweb.random.dto.RouteDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchCategoryService {

    private final RouteService routeService;
    private final SearchByKeywordService searchService;

    public Map<String, Object> selectCategory(SelectedCategory selectedCategory) throws JsonProcessingException {

        String[] categoriesFinal = new String[selectedCategory.getCategories().size()];

        for (int i = 0; i<selectedCategory.getCategories().size(); i++) {
            Random random = new Random();
            // random.nextInt(categories.get(i).size())
            categoriesFinal[i] = selectedCategory.getCategories().get(i).get(random.nextInt(selectedCategory.getCategories().get(i).size()));
        }



        SearchForm searchForm = new SearchForm(selectedCategory.getRegion(), categoriesFinal);
        List<PlaceDto> placeList = new ArrayList<>();

        log.info(searchForm.getLocal());
        for (String category : searchForm.getCategories()) {
            log.info("Category: " + category);
        }


        // 결과로 반환할 데이터 구조 생성
        Map<String, Object> response = new HashMap<>();


        for (int i = 0; i < searchForm.getCategories().length; i++) {
            String query = (i == 0) ? selectedCategory.getRegion() + ' ' +  searchForm.getCategories()[i] : searchForm.getCategories()[i];
            String x = (i == 0) ? null : placeList.get(i-1).getX();
            String y = (i == 0) ? null : placeList.get(i-1).getY();
            boolean isFirst = (i == 0);
            Optional<PlaceDto> op = searchService.searchPlacesByKeyword(query, x, y, isFirst);
            op.ifPresent(placeList::add);
            if(op.isEmpty()){
                response.put("info", 0);
                return response;
            }

        }

        log.info("placeList 확인: {}", placeList);
        log.info("placeList 확인 0번째: {}", placeList.get(0));

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
        placeInfo.add(placeURL);

        System.out.println("placeInfo" + placeInfo);


        response.put("route", routes);
        response.put("info", placeInfo);

        System.out.println("placeSerchController파일 플레이스 서치 response: " + response);

        //데이터 수정 예시.
        //Member member = memberOpt.get(); // Member 객체 가져오기
        //member.setName("현조");
        //memberRepository.save(member);

        log.info("리턴값 확인: {}", response);
        log.info("리턴값 확인 .route: {}", response.get("route"));
        log.info("리턴값 확인 .route toString: {}", response.get("route").toString());
        log.info("리턴값 확인 .info: {}", response.get("info"));
        log.info("리턴값 확인 .info toString: {}", response.get("info").toString());

        return response;

    }

}
