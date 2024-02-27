package com.etl.sfdc.config.model.dto;

import lombok.Data;

@Data
public class Oauth {
    String grant_type;
    String client_id;
    String client_secret;
    String username;
    String password;
}
