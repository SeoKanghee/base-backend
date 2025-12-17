package com.kelly.base.common.i18n.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * json 형태로 작성된 i18n resource 파일을 읽기 위한 확장 로직
 *
 * @author 서강희
 */
public class JsonReloadableMessageSource extends ReloadableResourceBundleMessageSource {

    private final ObjectMapper objectMapper;

    public JsonReloadableMessageSource(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        setFileExtensions(List.of(".json"));
    }

    /**
     * json 파일을 읽어서 property 로 변환
     *
     * @param resource the resource to load from
     * @param filename the original bundle filename (basename + Locale)
     * @return 변환된 properties
     * @throws IOException 파일 읽기 실패
     */
    @Override
    protected @NonNull Properties loadProperties(@NonNull Resource resource,
                                                 @NonNull String filename) throws IOException {
        Properties props = new Properties();
        try (InputStream is = resource.getInputStream()) {
            TypeReference<Map<String, String>> typeRefMap = new TypeReference<>() {
            };

            Map<String, String> jsonMap = objectMapper.readValue(is, typeRefMap);
            jsonMap.forEach(props::setProperty);
        }
        return props;
    }
}
