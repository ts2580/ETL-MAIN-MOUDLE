package com.etl.sfdc.config.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class ParamVo {
    private List<String> listUnderDML = new ArrayList<>();
    private String upperDML;
}
