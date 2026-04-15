package com.jihyeon.pawNest.ai.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiService {

    public String detectDogBreed(MultipartFile file) throws IOException {
        // 1. JSON 키 파일 로드 (resources 폴더의 파일명과 일치해야 함)
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ClassPathResource("google-key.json").getInputStream()
        );

        // 2. 클라이언트 설정에 인증 정보 주입
        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        // 3. 이미지 데이터 변환
        ByteString imgBytes = ByteString.readFrom(file.getInputStream());
        Image img = Image.newBuilder().setContent(imgBytes).build();

        // 4. 분석 기능(Label Detection) 설정
        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();

        // 5. API 호출 (설정된 settings를 반드시 인자로 전달!)
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create(settings)) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(List.of(request));
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    log.error("Google Vision API Error: {}", res.getError().getMessage());
                    return "분석 실패: " + res.getError().getMessage();
                }

                // 결과물 중 품종 키워드 추출
                String finalResult = res.getLabelAnnotationsList().stream()
                        .filter(label -> isDogBreed(label.getDescription()))
                        .limit(3)
                        .map(label -> {
                            String koreanName = translateToKorean(label.getDescription()); // 한글 번역 수행
                            int confidence = Math.round(label.getScore() * 100);
                            return String.format("%s (%d%%)", koreanName, confidence);
                        })
                        .collect(Collectors.joining(", "));

                return finalResult.isEmpty()? "품종을 특정할 수 없습니다. 다른 사진으로 재시도 바랍니다.": finalResult;
            }
        }
        return "분석 결과가 없습니다.";
    }

    private boolean isDogBreed(String label) {
        // AI가 분석한 결과 중 너무 포괄적이거나 상태를 나타내는 단어들
        List<String> excluded = List.of(
        // 1. 포괄적인 단어 (개과, 포유류 등)
                "Dog", "Puppy", "Dog breed", "Mammal", "Pet", "Vertebrate",
                "Canidae", "Carnivore", "Carnivores", "Snout", "Working animal",
                "Toy dog", "Companion dog", "Sporting group", "Non-sporting group",
                "Black", "White", "Fur", "Nose", "Eye"
        );

        // 대소문자 구분 없이 필터링하기 위해 equalsIgnoreCase 사용
        return excluded.stream()
                .noneMatch(excludedWord -> excludedWord.equalsIgnoreCase(label));
    }

    //번역 메소드
    private String translateToKorean(String englishText) {
        try {
            Translate translate = TranslateOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource("google-key.json").getInputStream()))
                    .build()
                    .getService();

            Translation translation = translate.translate(
                    englishText,
                    Translate.TranslateOption.sourceLanguage("en"),
                    Translate.TranslateOption.targetLanguage("ko")
            );
            return translation.getTranslatedText();
        } catch (Exception e) {
            log.error("번역 에러: {}", e.getMessage());
            return englishText; // 번역 실패 시 영문 그대로 반환
        }
    }
}