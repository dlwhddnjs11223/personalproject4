package com.sparta.ottoon.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.ottoon.auth.dto.KakaoUserInfoDto;
import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.entity.UserStatus;
import com.sparta.ottoon.auth.jwt.JwtUtil;
import com.sparta.ottoon.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Slf4j(topic = "SocialService")
@Service
@RequiredArgsConstructor
public class SocialService {
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${kakao.client.id}")
    private String client_id;

    public String kakaoLogin(String code) throws JsonProcessingException {
        // kakao로부터 카카오에 접속할 수 있는 accessToken을 받아온다.
        String accessToken = getToken(code);

        // 카카오 유저 정보 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 기존에 있는 회원이 아니라면 등록 시켜주기
        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

        // 우리 사이트에 접속할 수 있는 토큰 발급
        String createToken = JwtUtil.createToken(kakaoUser.getUsername(), JwtUtil.ACCESS_TOKEN_EXPIRATION);
        return createToken;
    }

    /**
     * 카카오에 접근할 수 있는 액세스 토큰 받아오기
     * @param code
     * @return
     * @throws JsonProcessingException
     */
    private String getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", client_id);
        body.add("redirect_uri", "http://localhost:8080/api/auth/kakao");
        body.add("code", code);

        // 카카오에 보낼 데이터
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        // 카카오에서 json 형태로 받아온 것을 읽는디.
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        return jsonNode.get("access_token").asText(); // 토큰 가져오기
    }

    /**
     * 카카오로부터 받은 액세스 토큰으로 카카오 유저 정보 가져오기
     * @param accessToken
     * @return
     * @throws JsonProcessingException
     */
    public KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/X-WWW-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String , String >> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties").get("nickname").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();

        return new KakaoUserInfoDto(id, nickname, email);
    }

    /**
     * 필요할 경우 DB에 유저를 등록
     * @param kakaoUserInfo
     * @return
     */
    @Transactional
    protected User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        Long kakaoId = kakaoUserInfo.getId();
        User kakaoUser = userRepository.findByKakaoId(kakaoId).orElse(null);

        if (kakaoUser == null) { // 등록되어 있는 카카오 유저가 없다면
            String kakaoEmail = kakaoUserInfo.getEmail();
            User sameEmailUser = userRepository.findByEmail(kakaoEmail).orElse(null);
            if (sameEmailUser != null) { // 같은 이메일을 가진 유저가 없다면
                kakaoUser = sameEmailUser;
                kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
            } else { // DB에 새로운 유저를 등록해준다.
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                String email = kakaoUserInfo.getEmail();

                String username = "kakao" + kakaoId;
                String refresh = JwtUtil.createToken(username, JwtUtil.REFRESH_TOKEN_EXPIRATION);
                kakaoUser = new User(username, kakaoUserInfo.getNickname(), encodedPassword, email, UserStatus.ACTIVE, kakaoId, refresh);
            }
            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }
}
