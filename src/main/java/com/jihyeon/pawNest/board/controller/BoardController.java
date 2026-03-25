package com.jihyeon.pawNest.board.controller;

import com.jihyeon.pawNest.board.service.BoardLikeService;
import com.jihyeon.pawNest.board.service.BoardService;
import com.jihyeon.pawNest.dto.request.board.BoardCreateRequest;
import com.jihyeon.pawNest.dto.request.board.BoardUpdateRequest;
import com.jihyeon.pawNest.dto.response.board.ApiResponse;
import com.jihyeon.pawNest.dto.response.board.BoardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "Board", description = "실종 동물 게시판 API")
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardLikeService boardLikeService;

    @Operation(summary = "실종 동물 게시글 등록", description = "동물 정보와 실종 장소를 등록합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Long> create(@RequestPart("request") BoardCreateRequest request,
                                    @RequestParam(required = false,name = "file") List<MultipartFile> files,
                                    @AuthenticationPrincipal String userId) {
        try {
            if(userId == null) return ApiResponse.error("로그인 후 이용 가능합니다.");
            Long id = boardService.create(request, files,userId);
            return ApiResponse.success("게시글 등록되었습니다.", id);
        }catch (Exception e){
            return ApiResponse.error("게시글 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Operation(summary = "실종 동물 게시글 목록 조회(페이징/검색)",
            description = "품종 검색 및 페이징 처리가 포함된 목록 조회입니다.")
    @GetMapping
    public Page<BoardResponse> list(
            @RequestParam(required = false)@Schema(example = "강아지") String breed1, // 검색 조건 (필수 X)
            @RequestParam(required = false)@Schema(example = "갈색") String color, // 검색 조건 (필수 X)
            @RequestParam(required = false) String searchText, // 검색 조건 (필수 X)
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return boardService.findAll(breed1,color,searchText, pageable);
    }

    @Operation(summary = "내가 쓴 게시글 목록 조회(페이징/검색)",
            description = "내가 쓴 글 목록 조회입니다.")
    @GetMapping("/myPosts")
    public Page<BoardResponse> myPosts(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return boardService.getMyPosts(userId, pageable);
    }

    @Operation(summary = "내 관심글 목록 조회(페이징/검색)",
            description = "관심글 목록 조회입니다.")
    @GetMapping("/myLikes")
    public Page<BoardResponse> myLikes(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return boardLikeService.getMyLikeBoards(userId, pageable);
    }

    @Operation(summary = "관심글 등록/해제",
            description = "관심글 등록/해제합니다.")
    @PostMapping("/like/{boardId}")
    public ApiResponse<Long> like(
            @AuthenticationPrincipal String userId,
            @PathVariable Long boardId
    ) {
        String result = boardLikeService.toggleLike(userId,boardId);
        return ApiResponse.success(result);
    }

    @Operation(summary = "게시글 상세 조회", description = "ID를 통해 특정 게시글의 상세 정보를 조회합니다.")
    @GetMapping("/{boardId}")
    public BoardResponse detail(@PathVariable Long boardId,@AuthenticationPrincipal String userId) {
        return boardService.findById(boardId,userId);
    }

    @Operation(summary = "게시글 수정", description = "등록된 실종 동물 정보를 수정합니다.")
    @PutMapping("/{boardId}")
    public ApiResponse<Long> update(@PathVariable Long boardId,
                                    @RequestPart("request")  BoardUpdateRequest request,
                                    @RequestParam(required = false, name= "file") List<MultipartFile> newFiles) {
        try {
            boardService.update(boardId, request, newFiles);
            return ApiResponse.success("게시글 수정되었습니다.");
        }catch (Exception e){
            return ApiResponse.error("게시글 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제 처리(Soft Delete)합니다.")
    @DeleteMapping("/{boardId}")
    public ApiResponse<Long> delete(@PathVariable Long boardId) {
        try {
            boardService.delete(boardId);
            return ApiResponse.success("게시글 삭제되었습니다.");
        }catch (Exception e){
            return ApiResponse.error("게시글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Operation(summary = "메인화면 인기글/최신글 5개 조회", description = "메인화면에 최신글/인기글 5개를 조회합니다.")
    @GetMapping("/main")
    public Map<String, List<BoardResponse>> mainList() {
        return boardService.mainList();
    }
}