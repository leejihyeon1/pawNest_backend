package com.jihyeon.pawNest.dto.response.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BreedResponse {
    private String breed;      // 품종 이름 (예: "Golden Retriever")
    private double confidence; // 정확도 (예: 0.98)
    private String message;    // 사용자에게 보여줄 메시지
}