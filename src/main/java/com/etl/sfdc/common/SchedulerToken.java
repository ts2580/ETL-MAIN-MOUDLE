package com.etl.sfdc.common;

import com.etl.sfdc.config.model.dto.Token;
import com.etl.sfdc.config.model.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulerToken {
    private final TokenService tokenService;

    @Scheduled(cron = "0 0 0/2 * * *")
    public void run() throws JsonProcessingException {
        Token tokenVo = new Token();
        tokenVo.setOrgType("esm");
        tokenService.getToken(tokenVo);
    }
}
