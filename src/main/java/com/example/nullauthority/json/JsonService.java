package com.example.nullauthority.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonPatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class JsonService {
    private static final Person person = new Person(1,"John","john@email.com","010-123-4567");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public PersonDto patch(JsonNode operation) throws JsonProcessingException {
        // 원래 entity dto 변환
//        JsonNode source = objectMapper.valueToTree(PersonDto.toDto(person));
//        JsonPatch.applyInPlace(patch, source);
        // patch
//        PersonDto dtoDiff = objectMapper.treeToValue(source, PersonDto.class);

//        PersonDto result = patch(operation, PersonDto.toDto(person));
        PersonDto result = PatchUtil.patch(operation, PersonDto.toDto(person));
        log.info("[JsonService] patch result: {}",result);
        person.update(result.name(), result.email(), result.phone());
        return PersonDto.toDto(person);
    }
}
