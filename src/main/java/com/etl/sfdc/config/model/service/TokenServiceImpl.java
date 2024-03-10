package com.etl.sfdc.config.model.service;

import com.etl.sfdc.config.model.dto.Token;
import com.etl.sfdc.config.model.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

    private final TokenRepository tokenRepository;

    public Token getToken(Token setVo) {
        Token getVo =  tokenRepository.getToken(setVo);
        System.out.printf("getVo>>>"+getVo);
        return getVo;
    }
}
















