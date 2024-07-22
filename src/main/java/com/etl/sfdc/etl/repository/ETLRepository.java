package com.etl.sfdc.etl.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ETLRepository {
    void setFieldDef(String ddl);
}
