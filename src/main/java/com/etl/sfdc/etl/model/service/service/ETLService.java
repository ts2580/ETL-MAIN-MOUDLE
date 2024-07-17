package com.etl.sfdc.etl.model.service.service;

import com.etl.sfdc.etl.model.dto.ObjectDefinition;

import java.util.List;

public interface ETLService {

    List<ObjectDefinition> getObjects() throws Exception;

    void setObjects(String selectedObject) throws Exception;
}
