package com.etl.sfdc.config.controller;

import com.etl.sfdc.config.model.dto.Token;
import com.etl.sfdc.config.model.dto.User;
import com.etl.sfdc.config.model.service.ConfigService;
import com.etl.sfdc.config.model.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("config")
public class ConfigController {

    private final ConfigService configService;

    private final TokenService tokenService;

    @GetMapping("/token")
    public Token getToken() throws JsonProcessingException {
        Token token = new Token();
        token = tokenService.getToken(token);
        System.out.println("token>>>" + token);
        return token;
    }

    @PostMapping("/user")
    public User hello(@RequestBody Map<String, Object> map) {

        User user = new User();

        try {
            user = configService.getUserDes(String.valueOf(map.get("userName")));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return user;
    }






}



















