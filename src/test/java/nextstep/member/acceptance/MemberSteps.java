package nextstep.member.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberSteps {
    private static final String AUTHORIZATION = "Authorization";

    public static ExtractableResponse<Response> 회원_생성_요청(String email, String password, Integer age) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("age", age + "");

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/members")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 회원_정보_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_정보_수정_요청(ExtractableResponse<Response> response, String email, String password, Integer age) {
        String uri = response.header("Location");

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("age", age + "");

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(uri)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 회원_삭제_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");
        return RestAssured
                .given().log().all()
                .when().delete(uri)
                .then().log().all().extract();
    }

    public static void 회원_정보_조회됨(ExtractableResponse<Response> response, String email, int age) {
        assertThat(response.jsonPath().getString("id")).isNotNull();
        assertThat(response.jsonPath().getString("email")).isEqualTo(email);
        assertThat(response.jsonPath().getInt("age")).isEqualTo(age);
    }

    public static ExtractableResponse<Response> login(String email, String password) {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("email", email);
        parameter.put("password", password);

        return RestAssured
                .given().log().all()
                    .body(parameter)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post("/login/token")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getMemberOfMine(ExtractableResponse<Response> loginResponse) {
        String token = loginResponse.jsonPath().getString("accessToken");
        return getMemberOfMine(token);
    }

    public static ExtractableResponse<Response> getMemberOfMine(String token) {
        return RestAssured
                .given().log().all()
                    .header(AUTHORIZATION, "Bearer " + token)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .get("/members/me")
                .then().log().all()
                .extract();
    }

    public static void assertMemberResponse(ExtractableResponse<Response> response, String email, int age) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        회원_정보_조회됨(response, email, age);
    }
}