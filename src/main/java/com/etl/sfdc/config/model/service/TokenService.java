package com.etl.sfdc.config.model.service;

import com.etl.sfdc.config.model.dto.Token;
import com.etl.sfdc.config.model.dto.User;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface TokenService {

    Token getToken(Token setToken) throws JsonProcessingException;
}
