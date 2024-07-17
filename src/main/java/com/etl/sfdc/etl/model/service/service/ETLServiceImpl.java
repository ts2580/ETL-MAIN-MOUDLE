package com.etl.sfdc.etl.model.service.service;

import com.etl.sfdc.common.SalesforceOAuth;
import com.etl.sfdc.common.UserSession;
import com.etl.sfdc.etl.model.dto.Definition;
import com.etl.sfdc.etl.model.dto.FieldDefinition;
import com.etl.sfdc.etl.model.dto.ObjectDefinition;
import com.etl.sfdc.etl.model.service.repository.ETLRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.util.EncodingUtils;
import org.springframework.stereotype.Service;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class ETLServiceImpl implements ETLService {

    private final UserSession userSession;

    private final ETLRepository etlRepository;
    private final String INSTANCE_URL = "https://ecologysyncmanagement-dev-ed.develop.my.salesforce.com";

    @Override
    public List<ObjectDefinition> getObjects() throws Exception {

        List<ObjectDefinition> listDef = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(INSTANCE_URL + "/services/data/v61.0/sobjects")
                .addHeader("Authorization", "Bearer " + SalesforceOAuth.getAccessToken())
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseBody = response.body().string();

            // 잭슨으로 역직렬화
            ObjectMapper objectMapper = new ObjectMapper();

            // 세일즈 포스로 따지면 JSON.deserializeUntyped();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            JsonNode sobjects = rootNode.get("sobjects");

            listDef = objectMapper.convertValue(sobjects, new TypeReference<List<ObjectDefinition>>() {});

        } else {
            System.err.println("오브젝트 목록 불러오기 실패 : " + response.message());
        }

        return listDef;
    }

    @Override
    public void setObjects(String selectedObject) throws Exception {

        List<FieldDefinition> listDef = new ArrayList<>();

        String query =
                "select Id, EntityDefinitionId, DeveloperName, QualifiedApiName, Label, Length, DataType, ValueTypeId, IsIndexed, EntityDefinition.QualifiedApiName " +
                        "from FieldDefinition " +
                        "where EntityDefinition.QualifiedApiName =" + '\'' +  selectedObject + "' ";
        String toolingSoqlSafe =  URLEncoder.encode(query, StandardCharsets.UTF_8);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(INSTANCE_URL + "/services/data/v61.0/tooling/query/?q=" + toolingSoqlSafe)
                .addHeader("Authorization", "Bearer " + SalesforceOAuth.getAccessToken())
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseBody = response.body().string();

            // 잭슨으로 역직렬화
            ObjectMapper objectMapper = new ObjectMapper();

            // 세일즈 포스로 따지면 JSON.deserializeUntyped();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            JsonNode sobjects = rootNode.get("records");

            listDef = objectMapper.convertValue(sobjects, new TypeReference<List<FieldDefinition>>() {});

            StringBuilder DDL = new StringBuilder();
            DDL.append("create table config.").append(selectedObject).append("(");

            // 아래에서 DML 만들때 쓸꺼임. 완전한 동적 DML 만들 떄 필요함
            Map<String, Object> mapType = new TreeMap<>();

            for(FieldDefinition obj : listDef){

                mapType.put(obj.QualifiedApiName, obj.ValueTypeId);

                // 세일즈포스에서 만드는 모든 필드타입들은 하단의 Type으로 모인다
                if(obj.QualifiedApiName.equals("Id")){
                    DDL.append("sfid VARCHAR(18) primary key not null comment '").append(obj.Label).append("',");
                } else if (obj.QualifiedApiName.equals("Description")) {
                    DDL.append(obj.QualifiedApiName).append(" TEXT comment '").append(obj.Label).append("',");
                }else if (obj.ValueTypeId.equals("id")) {
                    DDL.append(obj.QualifiedApiName).append(" VARCHAR(18) comment '").append(obj.Label).append("',");
                }else if (obj.ValueTypeId.equals("string")) {
                    DDL.append(obj.QualifiedApiName).append(" VARCHAR(").append(obj.Length).append(") comment '").append(obj.Label).append("',");
                }else if (obj.ValueTypeId.equals("boolean")) {
                    DDL.append(obj.QualifiedApiName).append(" boolean comment '").append(obj.Label).append("',");
                }else if (obj.ValueTypeId.equals("datetime")) {
                    DDL.append(obj.QualifiedApiName).append(" TIMESTAMP comment '").append(obj.Label).append("',");
                }else if (obj.ValueTypeId.equals("date")) {
                    DDL.append(obj.QualifiedApiName).append(" date comment '").append(obj.Label).append("',");
                }else if (obj.ValueTypeId.equals("time")) {
                    DDL.append(obj.QualifiedApiName).append(" time without time zone comment '").append(obj.Label).append("',");
                }else if (obj.ValueTypeId.equals("double")) {
                    DDL.append(obj.QualifiedApiName).append(" double precision comment '").append(obj.Label).append("',");
                }
            }

            DDL.deleteCharAt(DDL.length() - 1);
            DDL.append("); ");

            // 테이블 만들기
            etlRepository.setFieldDef(DDL.toString());

        } else {
            System.err.println("오브젝트 목록 불러오기 실패 : " + response.message());
        }

    }
}
