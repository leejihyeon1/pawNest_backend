package com.jihyeon.pawNest.chat.service;

import com.jihyeon.pawNest.board.repository.BoardRepository;
import com.jihyeon.pawNest.chat.repository.ChatMessageRepository;
import com.jihyeon.pawNest.chat.repository.ChatRoomRepository;
import com.jihyeon.pawNest.domain.board.Board;
import com.jihyeon.pawNest.domain.chat.ChatMessage;
import com.jihyeon.pawNest.domain.chat.ChatRoom;
import com.jihyeon.pawNest.dto.request.chat.ChatRoomRequest;
import com.jihyeon.pawNest.dto.response.chat.ChatRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final BoardRepository boardRepository;

    public ChatRoomResponse createChatRoom(ChatRoomRequest request,String userId) {
        // 1. 이미 존재하는 방인지 확인
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByBoardBoardIdAndSenderIdAndReceiverId(
                request.getBoardId(), userId, request.getReceiverId());

        // 2. 존재한다면 해당 방 정보를 바로 반환
        if (existingRoom.isPresent()) {
            return new ChatRoomResponse(existingRoom.get(),null);
        }

        // 3. 존재하지 않는다면 새로 생성
        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        ChatRoom newRoom = ChatRoom.builder()
                .board(board)
                .senderId(userId)
                .receiverId(request.getReceiverId())
                .build();

        chatRoomRepository.save(newRoom);
        return new ChatRoomResponse(newRoom,null);
    }

    public List<ChatRoomResponse> findAllRoomsByUserId(String userId) {
        // 내가 보낸 사람이거나 받은 사람인 모든 방을 찾음
        List<ChatRoom> chatRooms = chatRoomRepository.findAllBySenderIdOrReceiverIdOrderByRoomIdDesc(userId, userId);
        //각 방 별 마지막 채팅 메세지 세팅
        return chatRooms.stream().map(chatRoom -> {
            //해당 방의 가장 최신 메세지 조회
            Optional<ChatMessage> lastChat = chatMessageRepository.findTopByChatRoomOrderByCreatedAtDesc(chatRoom);

            //메세지가 있으면 내용 전달, 없으면 기본 문구 전달
            String lastMessage = lastChat.map(ChatMessage::getMessage).orElse("대화 시작 전입니다.");
            return new ChatRoomResponse(chatRoom,lastMessage);
        }).toList();
    }

}