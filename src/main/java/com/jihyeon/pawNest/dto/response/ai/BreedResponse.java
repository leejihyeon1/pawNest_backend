package com.jihyeon.pawNest.dto.response.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BreedResponse {
    private String title;      // 품종 이름 (예: "Golden Retriever")
    private double value; // 정확도 (예: 0.98)
}