package com.etl.sfdc.user.model.dto;

import lombok.Data;

@Data
public class Member {
    private Integer id;
    private String name;
    private String username;
    private String password;
    private String email;
    private String description;
}
