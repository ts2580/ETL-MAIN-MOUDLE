package com.etl.sfdc.etl.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldDefinition {
    public String Id;
    public String EntityDefinitionId;
    public String DeveloperName;
    public String QualifiedApiName;
    public String Label;
    public String Length;
    public String DataType;
    public String ValueTypeId;
    public String IsIndexed;
}
