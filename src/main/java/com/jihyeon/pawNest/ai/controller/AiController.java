package com.jihyeon.pawNest.ai.controller;

import com.jihyeon.pawNest.ai.service.AiService;
import com.jihyeon.pawNest.dto.response.ai.BreedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Search", description = "AI 품종 찾기")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * 이미지 파일을 받아 강아지 품종을 분석합니다.
     */
    @PostMapping(value = "/detect-breed", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지로 ai 품종 찾기")
    public List<BreedResponse> detectBreed(@RequestParam("file") MultipartFile file) throws Exception {
        // 1. 파일이 비어있는지 체크
        if (file.isEmpty()) {
            throw new Exception("파일이 업로드되지 않았습니다.");
        }

        try {
            // 2. aiService를 통해 분석 실행
            return aiService.detectDogBreed(file);

        } catch (IOException e) {
            // 4. 파일 읽기나 API 호출 중 에러 발생 시
            throw new Exception("AI 분석 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}