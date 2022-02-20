package nextstep.subway.unit;

import nextstep.auth.authentication.*;
import nextstep.auth.context.Authentication;
import nextstep.member.application.CustomUserDetailsService;
import nextstep.member.domain.LoginMember;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static nextstep.auth.context.SecurityContextHolder.SPRING_SECURITY_CONTEXT_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SessionAuthenticationInterceptorTest {
    private static final String EMAIL = "email@email.com";
    private static final String PASSWORD = "password";

    private final CustomUserDetailsService userDetailsService = mock(CustomUserDetailsService.class);

    @Test
    void convert() throws IOException {
        // given
        SessionAuthenticationInterceptor interceptor = new SessionAuthenticationInterceptor(userDetailsService);
        MockHttpServletRequest request = createMockRequest();

        // when
        AuthenticationToken authenticationToken = interceptor.convert(request);

        // then
        assertThat(authenticationToken.getPrincipal()).isEqualTo(EMAIL);
        assertThat(authenticationToken.getCredentials()).isEqualTo(PASSWORD);
    }

    @Test
    void authenticate() {
        // given
        SessionAuthenticationInterceptor interceptor = new SessionAuthenticationInterceptor(userDetailsService);
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(new LoginMember(1L, EMAIL, PASSWORD, 20));

        // when
        AuthenticationToken authenticationToken = AuthenticationToken.of(EMAIL, PASSWORD);
        Authentication authentication = interceptor.authenticate(authenticationToken);

        // then
        assertThat(authentication.getPrincipal()).isNotNull();
    }

    @Test
    void preHandle() throws IOException {
        // given
        SessionAuthenticationInterceptor interceptor = new SessionAuthenticationInterceptor(userDetailsService);

        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(new LoginMember(1L, EMAIL, PASSWORD, 20));

        // when
        MockHttpServletRequest request = createMockRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        interceptor.preHandle(request, response, new Object());

        // then
        assertThat(request.getSession().getAttribute(SPRING_SECURITY_CONTEXT_KEY)).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    }

    private MockHttpServletRequest createMockRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        Map<String, String> parameters = Map.of("username", EMAIL, "password", PASSWORD);
        request.setParameters(parameters);

        return request;
    }

}
