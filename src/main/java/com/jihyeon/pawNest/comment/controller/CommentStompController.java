package com.jihyeon.pawNest.comment.controller;

import com.jihyeon.pawNest.comment.service.CommentService;
import com.jihyeon.pawNest.dto.request.comment.CommentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentStompController {

     private final CommentService commentService;

     //댓글 전송
     //접두사 /app 필요!!!
    @MessageMapping("/comment/{boardId}")
    // 일반적인 @RestController :  @requestBody , STOMP(webSocket)환경 : @payload
    public void sendComment(@DestinationVariable Long boardId, @Payload CommentRequest request) {

        if(request.getContent().length() > 200){
            throw  new RuntimeException("댓글은 200자 이내로 입력해주세요.");
        }
        commentService.saveAndSendComment(boardId,request);
    }

    @MessageExceptionHandler(RuntimeException.class) //해당 컨트롤러 클래스 안에서 발생하는 에러만 잡음
//    @SendTo("/topic/errors") // /user/queue/errors 대신 공용 채널로 변경(테스트용)
    @SendToUser("/queue/errors") // 에러를 발생시킨 세션(사용자)에게만 전송
    public String handleException(RuntimeException ex) {

        log.error("검증 에러 발생: {}", ex.getMessage());
        return ex.getMessage(); // 이 문자열이 /user/queue/errors로 전송됨
    }
}