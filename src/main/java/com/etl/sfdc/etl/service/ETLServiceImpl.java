package com.etl.sfdc.etl.service;

import com.etl.sfdc.common.SalesforceOAuth;
import com.etl.sfdc.etl.dto.FieldDefinition;
import com.etl.sfdc.etl.dto.ObjectDefinition;
import com.etl.sfdc.etl.repository.ETLRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ETLServiceImpl implements ETLService {

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


        try(Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();

                // 잭슨으로 역직렬화
                ObjectMapper objectMapper = new ObjectMapper();

                // 세일즈 포스로 따지면 JSON.deserializeUntyped();
                JsonNode rootNode = objectMapper.readTree(responseBody);

                JsonNode sobjects = rootNode.get("sobjects");

                listDef = objectMapper.convertValue(sobjects, new TypeReference<List<ObjectDefinition>>(){});

            } else {
                System.err.println("오브젝트 목록 불러오기 실패 : " + response.message());
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return listDef;
    }

    @Override
    public void setObjects(String selectedObject) throws Exception {

        List<FieldDefinition> listDef = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(INSTANCE_URL + "/services/data/v61.0/sobjects/" + selectedObject + "/describe")
                .addHeader("Authorization", "Bearer " + SalesforceOAuth.getAccessToken())
                .addHeader("Content-Type", "application/json")
                .build();

        StringBuilder soql = new StringBuilder();

        try(Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            // 잭슨으로 역직렬화
            ObjectMapper objectMapper = new ObjectMapper();

            // 세일즈 포스로 따지면 JSON.deserializeUntyped();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            JsonNode fields = rootNode.get("fields");

            listDef = objectMapper.convertValue(fields, new TypeReference<List<FieldDefinition>>() {});

            StringBuilder ddl = new StringBuilder();
            ddl.append("create table config.").append(selectedObject).append("(");

            for(FieldDefinition obj : listDef){

                soql.append(obj.name).append(",");

                // 세일즈포스에서 만드는 모든 필드타입들은 하단의 Type으로 모인다
                if(obj.type.equals("id")){
                    ddl.append("sfid VARCHAR(18) primary key not null comment '").append(obj.label).append("',");
                } else if (obj.name.equals("Description")) {
                    ddl.append(obj.name).append(" TEXT comment '").append(obj.label).append("',");
                }else if (obj.type.equals("reference")) {
                    ddl.append(obj.name).append(" VARCHAR(18) comment '").append(obj.label).append("',");
                }else if (obj.type.equals("string")) {
                    ddl.append(obj.name).append(" VARCHAR(").append(obj.length).append(") comment '").append(obj.label).append("',");
                }else if (obj.type.equals("boolean")) {
                    ddl.append(obj.name).append(" boolean comment '").append(obj.label).append("',");
                }else if (obj.type.equals("datetime")) {
                    ddl.append(obj.name).append(" TIMESTAMP comment '").append(obj.label).append("',");
                }else if (obj.type.equals("date")) {
                    ddl.append(obj.name).append(" date comment '").append(obj.label).append("',");
                }else if (obj.type.equals("time")) {
                    ddl.append(obj.name).append(" time comment '").append(obj.label).append("',");
                }else if (obj.type.equals("double")) {
                    ddl.append(obj.name).append(" double precision comment '").append(obj.label).append("',");
                }
            }

            ddl.deleteCharAt(ddl.length() - 1);
            ddl.append("); ");

            soql.deleteCharAt(soql.length() - 1);
        }catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        // 테이블 만들기
        // etlRepository.setFieldDef(ddl.toString());

        String query = "SELECT " + soql.toString() + " FROM " + selectedObject;

        request = new Request.Builder()
                .url(INSTANCE_URL + "/services/data/v61.0/query/?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8))
                .addHeader("Authorization", "Bearer " + SalesforceOAuth.getAccessToken())
                .addHeader("Content-Type", "application/json")
                .build();

        try(Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}