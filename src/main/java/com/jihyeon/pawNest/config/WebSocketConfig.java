package com.jihyeon.pawNest.config;

import com.jihyeon.pawNest.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;

    //토큰에서 아이디 가져오는 @AuthenticationPrincipal을 가로채서
    //웹소켓 방식에서도 사용 가능하도록 핸들러 추가
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

    //stomp 구조 : 구독(수신대기) - 발행(댓글 입력) - 브로커(댓글을 수신대기로 전달)
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 처음 웹소켓 연결을 맺을 때 사용할 엔드포인트
        // ws://localhost:8080/ws-stomp/websocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 모든 도메인 허용 (테스트용)
                .withSockJS(); // SockJS 지원 (브라우저 호환성) websocket 추가 필요
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 서버에서 클라이언트로 메시지를 보낼 때 (구독 경로)
        registry.enableSimpleBroker("/topic");

        // 클라이언트에서 서버로 메시지를 보낼 때 (발행 경로)
        registry.setApplicationDestinationPrefixes("/app");
    }
}