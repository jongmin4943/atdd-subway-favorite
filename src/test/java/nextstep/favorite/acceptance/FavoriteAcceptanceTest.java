package nextstep.favorite.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.common.api.FavoriteApiHelper;
import nextstep.common.api.LineApiHelper;
import nextstep.common.api.SectionApiHelper;
import nextstep.common.api.StationApiHelper;
import nextstep.core.AcceptanceTest;
import nextstep.core.AcceptanceTestAuthBase;
import nextstep.core.RestAssuredHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("즐겨찾기 관련 기능")
@AcceptanceTest
public class FavoriteAcceptanceTest extends AcceptanceTestAuthBase {
    private Long 강남역_Id;
    private Long 교대역_Id;
    private Long 양재역_Id;
    private Long 남부터미널역_Id;
    private Long 사당역_Id;
    private Long 서울역_Id;

    /**
     * Given 지하철 노선을 생성하고
     */
    @BeforeEach
    void setUp() {
        // given
        교대역_Id = RestAssuredHelper.getIdFromBody(StationApiHelper.createStation("교대역"));
        강남역_Id = RestAssuredHelper.getIdFromBody(StationApiHelper.createStation("강남역"));
        양재역_Id = RestAssuredHelper.getIdFromBody(StationApiHelper.createStation("양재역"));
        남부터미널역_Id = RestAssuredHelper.getIdFromBody(StationApiHelper.createStation("남부터미널역"));
        서울역_Id = RestAssuredHelper.getIdFromBody(StationApiHelper.createStation("서울역"));
        사당역_Id = RestAssuredHelper.getIdFromBody(StationApiHelper.createStation("사당역"));
        RestAssuredHelper.getIdFromBody((LineApiHelper.createLine("2호선", "green", 교대역_Id, 강남역_Id, 5)));
        RestAssuredHelper.getIdFromBody((LineApiHelper.createLine("신분당선", "red", 강남역_Id, 양재역_Id, 10)));
        final Long 삼호선_Id = RestAssuredHelper.getIdFromBody((LineApiHelper.createLine("3호선", "orange", 교대역_Id, 남부터미널역_Id, 2)));
        SectionApiHelper.createSection(삼호선_Id, 남부터미널역_Id, 양재역_Id, 3);
    }


    /**
     * Given 로그인 정보와 함께
     * When 출발역과 도착역을 통해 경로를 즐겨찾기에 추가 하면
     * Then 즐겨찾기 목록 조회 시 생성한 즐겨찾기를 찾을 수 있다
     */
    @DisplayName("출발역과 도착역을 통해 즐겨찾기를 추가 할 수 있다.")
    @Test
    void 즐겨찾기_추가_성공_테스트() {
        // when
        final ExtractableResponse<Response> response = 즐겨찾기_추가_요청_With_로그인(강남역_Id, 남부터미널역_Id);

        // then
        즐겨찾기_추가_요청이_성공한다(response);

        // then
        즐겨찾기_목록_조회_시_생성한_즐겨찾기를_찾을_수_있다(response);
    }

    /**
     * Given 로그인 정보 없이
     * When 출발역과 도착역을 통해 경로를 즐겨찾기에 추가 하면
     * Then 에러가 난다
     */
    @DisplayName("로그인이 되어있지 않으면 즐겨찾기를 추가 할 수 없다.")
    @Test
    void 로그인_안된_상태는_즐겨찾기_추가에_실패한다() {
        // when
        final ExtractableResponse<Response> response = 즐겨찾기_목록_조회_요청_Without_로그인(강남역_Id, 남부터미널역_Id);

        // then
        즐겨찾기_추가_요청이_거부된다(response);
    }

    /**
     * Given 로그인 정보와 함께
     * When 출발역과 도착역을 통해 경로를 즐겨찾기에 추가 하는데
     * When 존재하지 않는 경로인 경우
     * Then 에러가 난다
     */
    @DisplayName("존재하지 않는 경로는 즐겨찾기에 추가 할 수 없다.")
    @Test
    void 존재하지_않는_경로는_즐겨찾기_추가에_실패한다() {
        // when
        final ExtractableResponse<Response> response = 즐겨찾기_추가_요청_With_로그인(사당역_Id, 서울역_Id);

        // then
        즐겨찾기_추가_요청이_실패한다(response);
    }

    /**
     * Given 로그인 정보와 함께
     * When 출발역과 도착역을 통해 경로를 즐겨찾기에 추가 하는데
     * When 이미 추가된 즐겨찾기 경로라면
     * Then 에러가 난다
     */
    @DisplayName("이미 추가된 즐겨찾기 경로라면 즐겨찾기에 추가 할 수 없다.")
    @Test
    void 이미_추가된_즐겨찾기_경로라면_즐겨찾기_추가에_실패한다() {
        // given
        final ExtractableResponse<Response> firstResponse = 즐겨찾기_추가_요청_With_로그인(강남역_Id, 남부터미널역_Id);
        즐겨찾기_추가_요청이_성공한다(firstResponse);

        // when
        final ExtractableResponse<Response> secondResponse = 즐겨찾기_추가_요청_With_로그인(강남역_Id, 남부터미널역_Id);

        // then
        즐겨찾기_추가_요청이_실패한다(secondResponse);
    }

    private void 즐겨찾기_목록_조회_시_생성한_즐겨찾기를_찾을_수_있다(final ExtractableResponse<Response> response) {
        final Long createdId = RestAssuredHelper.getIdFromHeader(response);
        final ExtractableResponse<Response> listResponse = 즐겨찾기_목록_조회_요청_With_로그인();
        final List<Long> ids = listResponse.jsonPath().getList("id", Long.class);
        assertThat(ids).containsAnyOf(createdId);
    }

    private ExtractableResponse<Response> 즐겨찾기_목록_조회_요청_With_로그인() {
        return FavoriteApiHelper.fetchFavorites(accessToken);
    }

    private ExtractableResponse<Response> 즐겨찾기_추가_요청_With_로그인(final Long sourceId, final Long targetId) {
        return FavoriteApiHelper.addFavorite(accessToken, sourceId, targetId);
    }

    private ExtractableResponse<Response> 즐겨찾기_목록_조회_요청_Without_로그인(final Long sourceId, final Long targetId) {
        return FavoriteApiHelper.addFavorite(accessToken, sourceId, targetId);
    }

    private void 즐겨찾기_추가_요청이_성공한다(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 즐겨찾기_추가_요청이_거부된다(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private void 즐겨찾기_추가_요청이_실패한다(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
