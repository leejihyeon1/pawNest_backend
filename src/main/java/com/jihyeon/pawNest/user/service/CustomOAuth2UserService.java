package com.jihyeon.pawNest.user.service;

import com.jihyeon.pawNest.domain.user.Role;
import com.jihyeon.pawNest.domain.user.User;
import com.jihyeon.pawNest.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 1. 카카오에서 준 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        // 2. 이미 가입된 유저인지 확인 후 저장 또는 업데이트
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");

        // 이메일 파싱 로직: "user@kakao.com" -> "user"
        String parsedId = email.split("@")[0];

        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(nickname, parsedId))
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .nickname(nickname)
                            .userId(parsedId)
                            .role(Role.USER)
                            .build();

                    return userRepository.save(newUser);
                });

        return oAuth2User;
    }
}