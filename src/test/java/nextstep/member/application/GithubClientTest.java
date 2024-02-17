package nextstep.member.application;

import nextstep.common.exception.UnauthorizedException;
import nextstep.member.application.dto.GithubProfileResponse;
import nextstep.utils.GithubResponses;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext
public class GithubClientTest {

    @Autowired
    private GithubClient githubClient;

    @Test
    @DisplayName("requestGithubToken 를 통해 access_token 을 발급 받을 수 있다.")
    void requestGithubToken() {
        final String githubToken = githubClient.requestGithubToken(GithubResponses.사용자1.getCode());

        assertThat(githubToken).isEqualTo(GithubResponses.사용자1.getAccessToken());
    }

    @Test
    @DisplayName("requestGithubToken 를 통해 잘못된 code 를 보내면 UnauthorizedException 이 난다.")
    void requestGithubTokenWithWrongCode() {
        assertThatThrownBy(() -> githubClient.requestGithubToken(GithubResponses.잘못된_사용자.getCode()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("requestGithubProfile 를 통해 user github profile 정보를 반환받을 수 있다.")
    void requestGithubProfile() {
        final GithubProfileResponse profileResponse = githubClient.requestGithubProfile(GithubResponses.사용자1.getAccessToken());

        assertSoftly(softly -> {
            softly.assertThat(profileResponse.getEmail()).isEqualTo(GithubResponses.사용자1.getEmail());
            softly.assertThat(profileResponse.getAge()).isEqualTo(GithubResponses.사용자1.getAge());
        });
    }

}
