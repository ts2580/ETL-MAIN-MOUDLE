package com.etl.sfdc.config.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class User {
    @NotNull
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String email;

    @NotNull
    private String description;
}
