package com.etl.sfdc.config.model.repository;

import com.etl.sfdc.config.model.dto.Token;
import com.etl.sfdc.config.model.dto.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TokenRepository {

    Token getToken(Token setVo);
}
