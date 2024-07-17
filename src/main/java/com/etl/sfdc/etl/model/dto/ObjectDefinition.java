package com.etl.sfdc.etl.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjectDefinition {
    public String label;
    public String labelPlural;
    public String name;
}
