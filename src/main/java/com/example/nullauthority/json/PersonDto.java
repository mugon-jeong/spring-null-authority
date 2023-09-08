package com.example.nullauthority.json;

public record PersonDto(
        String name,
        String email,
        String phone
) {

    public static PersonDto toDto(Person person){
        return new PersonDto(person.getName(), person.getEmail(), person.getPhone());
    }
}
