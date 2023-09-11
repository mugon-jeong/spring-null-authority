package com.example.nullauthority.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonPatch;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PatchUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     *
     * @param patch patch 명령어
     * @param target patch 대상
     * @return 반영 결과
     * @param <T> 반환 타입
     * @throws JsonProcessingException Missing field error
     */

    public static <T> T patch(JsonNode patch, T target) throws JsonProcessingException {
        log.info("[PatchUtil] patch: " + patch);
        JsonNode source = objectMapper.valueToTree(target);
        JsonPatch.applyInPlace(patch, source);
        return objectMapper.treeToValue(source, (Class<T>) target.getClass());
    }
}
