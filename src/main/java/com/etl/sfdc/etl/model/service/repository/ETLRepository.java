package com.etl.sfdc.etl.model.service.repository;

import com.etl.sfdc.user.model.dto.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface ETLRepository {
    void setFieldDef(String ddl);
}
