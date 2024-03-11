package com.etl.sfdc.config.model.service;

import com.etl.sfdc.config.model.dto.Token;
import com.etl.sfdc.config.common.Auth;
import com.etl.sfdc.config.model.repository.TokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

    private final TokenRepository tokenRepository;

    public Token getToken(Token setVo) throws JsonProcessingException {

        Token getVo =  tokenRepository.getToken(setVo);
        System.out.printf("getVo123123213>>>"+getVo);
        getVo = Auth.getToken(getVo);

        Token returnVo = setToken(getVo);

        return returnVo;
    }

    public Token setToken(Token setVo) throws JsonProcessingException {
        setVo.setOrgType("esm");
        tokenRepository.setToken(setVo);
        Token getVo =  tokenRepository.getToken(setVo);
        return getVo;
    }
}
















