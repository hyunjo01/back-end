package capstone.courseweb.ai;

import capstone.courseweb.jwt.config.JwtAuthProvider;
import capstone.courseweb.jwt.utility.JwtIssuer;
import capstone.courseweb.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



import java.util.*;

@RestController
@RequiredArgsConstructor
public class PreferTestController {

    private final JwtAuthProvider jwtAuthProvider;
    private final JwtIssuer jwtIssuer;
    private final PreferenceService preferenceService;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;

    @PostMapping("/test-result")
    public ResponseEntity<Map<String, List<Object>>> receiveTestResult(@RequestBody Map<String, Object> testResult) { //, @RequestHeader("Authorization")String token

        /*
        //jwt 토큰 검증
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }

        String nickname = authentication.getName(); // 사용자의 id 가져오기 (JwtAuthProvider에서 사용자 ID를 subject로 저장한 경우)
        System.out.println("jwt 토큰 검증 받은 사용자 id" + nickname);

        Optional<Member> memberOpt = memberRepository.findByNickname(nickname);
        if (memberOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

         */


        //jwt 토큰 검증
        /*if (!jwtAuthProvider.validateToken(token.substring(7))) { //Bearer<토큰값>으로 전송되기 때문에 7번째 위치부터(토큰값만 추출)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token"); //HTTP 401 Unauthorized 상태 코드를 반환
        }
        //사용자 가져오기
        Claims claims = jwtIssuer.getClaims(token);
        String id = claims.get("id", String.class);
        //Member member;
        Optional<Member> memberOpt = memberRepository.findById(id);*/

        System.out.println("플라스크에 전송");

        String flaskResponse = preferenceService.sendToFlaskServer(testResult);
        JSONObject flaskResponseJson = new JSONObject(flaskResponse);

        // TODO: user vector 저장하는 코드
        String userVector = flaskResponseJson.get("user_vector").toString();




        JSONArray jsonArray = new JSONArray(flaskResponseJson.getJSONArray("placeID"));
        int[] intArray = new int[jsonArray.length()];

        // JSONArray 값들을 int로 변환하여 배열에 저장
        for (int i = 0; i < jsonArray.length(); i++) {
            intArray[i] = jsonArray.getInt(i);
        }

        Map<String, List<Object>> finalResponse = new HashMap<>();
        List<Object> places_info = new ArrayList<>();

        for (int i = 0; i < intArray.length; i++){
            Optional<Place> place = placeRepository.findById(intArray[i]);

            if (place.isPresent()) {
                Map<String, Object> newResponse = new HashMap<>();
                newResponse.put("placename", place.get().getName());
                newResponse.put("category", place.get().getCategory());
                newResponse.put("tag", place.get().getTag());
                newResponse.put("URL", "https://pcmap.place.naver.com/restaurant/"+place.get().getId());
                places_info.add(newResponse);
                System.out.println(newResponse);
            }
        }
        finalResponse.put("ai_recommend", places_info);

        return ResponseEntity.ok(finalResponse);

    }
}
