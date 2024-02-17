package nextstep.member.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class GithubClientTest {

    @Autowired
    private GithubClient githubClient;


    @Test
    @DisplayName("requestGithubToken 를 통해 access_token 을 발급 받을 수 있다.")
    void requestGithubToken() {
        final String code = "code";

        final String githubToken = githubClient.requestGithubToken(code);

        assertThat(githubToken).isNotBlank();
    }

}
