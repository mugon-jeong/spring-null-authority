package com.example.nullauthority.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class JsonController {
    public JsonController(JsonService jsonService) {
        this.jsonService = jsonService;
    }

    private final JsonService jsonService;

    /**
     * @param patch
     * [
     *     {
     *         "op": "replace",
     *         "path": "/name",
     *         "value":"json"
     *     },
     *     {
     *         "op": "remove",
     *         "path": "/email"
     *     }
     * ]
     */
    @PatchMapping("/json")
    public ResponseEntity<PersonDto> patch(@RequestBody JsonNode patch) throws JsonProcessingException {
        PersonDto dto = jsonService.patch(patch);
        return ResponseEntity.ok().body(dto);
    }
}
