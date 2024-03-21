package com.etl.sfdc.temp.controller;

import com.etl.sfdc.temp.model.service.TempService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("http-client")
public class TempRestController {

    private final TempService tempService;

    @GetMapping("http-url-connection")
    public String tryHttpUrlConnection() {

        long totalTime = 0;

        for (int i = 0; i < 30; i++) {
            totalTime += tempService.tryHttpUrlConnection("1");
        }

        for (int i = 0; i < 30; i++) {
            totalTime += tempService.tryHttpUrlConnection("2");
        }

        for (int i = 0; i < 30; i++) {
            totalTime += tempService.tryHttpUrlConnection("3");
        }

        return "총 실행 시간(밀리초) : " + totalTime;
    }

    @GetMapping("web-client")
    public String tryWebClient() {

        long totalTime = 0;

        for (int i = 0; i < 30; i++) {
            totalTime += tempService.tryWebClient("1");
        }

        for (int i = 0; i < 30; i++) {
            totalTime += tempService.tryWebClient("2");
        }

        for (int i = 0; i < 30; i++) {
            totalTime += tempService.tryWebClient("3");
        }

        return "총 실행 시간(밀리초) : " + totalTime;
    }





}
