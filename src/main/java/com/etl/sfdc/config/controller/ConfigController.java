package com.etl.sfdc.config.controller;

import com.etl.sfdc.config.model.dto.User;
import com.etl.sfdc.config.model.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService    ;//as



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



















