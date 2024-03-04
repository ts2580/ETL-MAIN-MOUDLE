package com.etl.sfdc.config.model.dto;

import lombok.Data;

@Data
public class Token {

    // OAuth 정보 송신시 받아오는 Access Token 값

    private String access_token;
    private String instance_url;
    private String id;
    private String token_type;
    private String issued_at;
    private String signature;
}
