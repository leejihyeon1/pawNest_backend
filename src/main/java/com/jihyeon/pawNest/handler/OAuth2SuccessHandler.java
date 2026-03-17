package com.jihyeon.pawNest.handler;

import com.jihyeon.pawNest.domain.user.User;
import com.jihyeon.pawNest.security.JwtTokenProvider;
import com.jihyeon.pawNest.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
       try {
           OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        // DB에서 유저 정보를 다시 조회 (parsedId 등을 가져오기 위해)
        User user = userRepository.findByEmail(email).orElseThrow();

        // 1. JWT 토큰 생성 (parsedId를 담아서!) ✅
        String token = tokenProvider.createToken(user.getUserId(), user.getNickname());
        // todo 2. 프론트엔드 리다이렉트 (쿼리 스트링으로 토큰 전달)
        // 프론트엔드 주소가 http://localhost:3000 이라면:
//        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth/callback")
//                .queryParam("token", token)
//                .build().toUriString();
//
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);

        // 테스트용으로 그냥 토큰 값을 브라우저 화면에 직접 찍어줄 수도 있습니다.
        response.setContentType("application/json;charset=UTF-8");
           String jsonResponse = String.format(
                   "{\"message\": \"로그인 성공\", \"token\": \"%s\", \"userId\": \"%s\"}",
                   token, user.getUserId()
           );

           response.getWriter().write(jsonResponse);
       }catch (Exception e) {
           e.printStackTrace();
       }
    }
}