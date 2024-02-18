package nextstep.auth.application;

import nextstep.auth.application.dto.AuthResponse;
import nextstep.common.exception.UnauthorizedException;
import nextstep.member.application.JwtTokenProvider;

public class AuthService {

    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final GithubOAuth2Client githubOAuth2Client;

    public AuthService(final UserDetailsService userDetailsService, final JwtTokenProvider jwtTokenProvider, final GithubOAuth2Client githubOAuth2Client) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.githubOAuth2Client = githubOAuth2Client;
    }

    public AuthResponse login(final String email, final String password) {
        final UserDetail userDetail = userDetailsService.loadUserByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("아이디와 비밀번호를 확인해주세요."));

        if (userDetail.isPasswordMismatch(password)) {
            throw new UnauthorizedException("아이디와 비밀번호를 확인해주세요.");
        }

        final String token = jwtTokenProvider.createToken(userDetail.getId(), userDetail.getEmail());

        return new AuthResponse(token);
    }

    public AuthResponse loginGithub(final String code) {
        return new AuthResponse("access_token");
    }
}
