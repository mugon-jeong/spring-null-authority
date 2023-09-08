package com.example.nullauthority.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonPatch;

public class PatchUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T patch(JsonNode patch, T target) throws JsonProcessingException {
        JsonNode source = objectMapper.valueToTree(target);
        JsonPatch.applyInPlace(patch, source);
        return objectMapper.treeToValue(source, (Class<T>) target.getClass());
    }
}
