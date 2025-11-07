package com.kelly.base.common.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kelly.base.common.exception.CommonException;
import com.kelly.base.common.response.CommonResultCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtil {
    public static final ObjectMapper objectMapper;

    static {
        // CommonBeanConfig 를 통해 Spring 에서 관리되고 주입되는 ObjectMapper 이므로 설정 변경시 전체 영향도 확인 필요
        objectMapper = new ObjectMapper();

        // 필드명에 따옴표가 없어도 파싱 가능 -> "{name: \"John\", age: 30}"
        objectMapper.configure(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(), true);

        // 특수 숫자값 허용 ( NaN, Infinity, -Infinity ) -> "value" : NaN
        objectMapper.configure(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature(), true);

        // ISO-8601 문자열 형식으로 직렬화하지 않음 -> "date": 1730803800000 (X)
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // getter 가 없는 빈 객체 직렬화시 에러 발생하지 않음
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 역직렬화 시 원본 데이터에 없는 값이 있어도 무시하고 처리
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // BigDecimal을 평문으로 출력 (과학적 표기법 방지) -> 0.00000123 (O) / 1.23E-6 (X)
        objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);

        // Java 8 날짜 관련 class ( LocalDate, LocalDateTime, ZonedDateTime.. ) 의 serialize/deserialize 지원
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static String convert(Object obj, boolean isPretty) throws CommonException {
        if (obj == null) {
            return null;    // "null" 로 String 이 반환되지 않도록 null 은 따로 처리
        }
        return isPretty ? convertPretty(obj) : convert(obj);
    }

    private static String convert(Object obj) throws CommonException {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new CommonException(CommonResultCode.SYSTEM_ERROR, getErrorMessage(e));
        }
    }

    private static String convertPretty(Object obj) throws CommonException {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            throw new CommonException(CommonResultCode.SYSTEM_ERROR, getErrorMessage(e));
        }
    }

    public static <T> T parse(String jsonStr, Class<T> clazz) throws CommonException {
        try {
            return objectMapper.readValue(jsonStr, clazz);
        } catch (Exception e) {
            throw new CommonException(CommonResultCode.SYSTEM_ERROR, getErrorMessage(e));
        }
    }

    public static <T> T parse(String jsonStr, TypeReference<T> ref) throws CommonException {
        try {
            return objectMapper.readValue(jsonStr, ref);
        } catch (Exception e) {
            throw new CommonException(CommonResultCode.SYSTEM_ERROR, getErrorMessage(e));
        }
    }

    private static String getErrorMessage(final Exception e) {
        return "[" + e.getClass().getSimpleName() + "] " + e.getMessage();
    }
}
