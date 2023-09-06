package com.example.nullauthority.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    @GetMapping("/api/public/super")
    public String superUser(){
        return "super";
    }

    @GetMapping("/api/public/admin")
    public String manager(){
        return "manager";
    }
    @GetMapping("/api/public/user")
    public String user(){
        return "user";
    }

    @GetMapping("/api/public/test")
    public String test(){
        return "test";
    }

    @GetMapping("/api/public/test2")
    public String test2(){
        return "test2";
    }
}
