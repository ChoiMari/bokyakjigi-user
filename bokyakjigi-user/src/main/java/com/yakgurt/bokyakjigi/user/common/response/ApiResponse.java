package com.yakgurt.bokyakjigi.user.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * 정상 응답 공통 API 포맷 클래스
 * 사용 목적 : 모든 API 응답을 일관된 형태로 제공하기 위해서 사용함
 * 프론트엔드 개발자와 협업 시 응답 구조를 명확히 정의함으로써
 * 예측 가능한 데이터 구조를 제공
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 JSON에서 제외
@AllArgsConstructor
@Schema(description = "표준 API 응답 래퍼")
public class ApiResponse <T> {

    @Schema(description = "응답 코드", example = "200 또는 201")
    private final int code; // HTTP 상태 코드

    @Schema(description = "상태 메시지", example = "OK 또는 CREATED")
    private final String status; // 상태 타입

    @Schema(description = "상세 메시지", example = "성공")
    private final String message; // 응답 메시지

    @Schema(description = "실제 데이터")
    private final T data; // 실제 응답 데이터

    @Schema(description = "응답 생성 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private final ZonedDateTime timestamp; //응답 생성 시간


    /**
     * 성공 응답을 생성하는 메서드
     * @param data 응답 데이터
     * @return 성공 상태의 ResponseDto
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                StatusCode.OK.getHttpStatus().value(),
                StatusCode.OK.name(),
                "요청이 성공적으로 처리되었습니다.",
                data,
                ZonedDateTime.now(ZoneOffset.UTC)
        );
    }

}
