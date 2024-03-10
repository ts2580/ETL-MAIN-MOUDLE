package com.etl.sfdc.config.model.service;

import com.etl.sfdc.config.model.dto.Token;
import com.etl.sfdc.config.model.dto.User;

public interface TokenService {

    Token getToken(Token setToken);
}
