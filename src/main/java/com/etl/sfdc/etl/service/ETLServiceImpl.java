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
import java.time.Duration;
import java.time.Instant;
import java.util.*;

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

        // 잭슨으로 역직렬화
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode;

        Request request = new Request.Builder()
                .url(INSTANCE_URL + "/services/data/v61.0/sobjects/" + selectedObject + "/describe")
                .addHeader("Authorization", "Bearer " + SalesforceOAuth.getAccessToken())
                .addHeader("Content-Type", "application/json")
                .build();

        // DDL 테이블 생성용
        StringBuilder ddl = new StringBuilder();

        // 순차적 values 구문 구성용
        List<String> listFields = new ArrayList<>();

        // soql로 받아온 값 변환용 맵
        Map<String,String> mapType = new HashMap<>();

        try(Response response = client.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body()).string();

            // 세일즈 포스로 따지면 JSON.deserializeUntyped();
            rootNode = objectMapper.readTree(responseBody);

            JsonNode fields = rootNode.get("fields");

            listDef = objectMapper.convertValue(fields, new TypeReference<List<FieldDefinition>>() {});

            ddl.append("CREATE OR REPLACE table config.").append(selectedObject).append("(");

            for(FieldDefinition obj : listDef){

                mapType.put(obj.name, obj.type);

                // 데이터 타입 확인용
                // System.out.println(obj.name + " : " + obj.type);

                // 세일즈포스에서 만드는 모든 필드타입들은 하단의 Type으로 모인다. 특정 Object 타입(Address, Name 등)은 빼주자
                switch (obj.type) {
                    case "id" -> {
                        ddl.append("sfid VARCHAR(18) primary key not null comment '").append(obj.label).append("',");
                    }case "textarea" -> {
                        if (obj.length > 4000) {
                            ddl.append(obj.name).append(" TEXT comment '").append(obj.label).append("',");
                        } else {
                            ddl.append(obj.name).append(" VARCHAR(").append(obj.length).append(") comment '").append(obj.label).append("',");
                        }
                        listFields.add(obj.name);
                    }case "reference" ->{
                        ddl.append(obj.name).append(" VARCHAR(18) comment '").append(obj.label).append("',");
                        listFields.add(obj.name);
                    }case "string", "picklist", "multipicklist", "phone", "url" ->{
                        ddl.append(obj.name).append(" VARCHAR(").append(obj.length).append(") comment '").append(obj.label).append("',");
                        listFields.add(obj.name);
                    }case "boolean" -> {
                        ddl.append(obj.name).append(" boolean comment '").append(obj.label).append("',");
                        listFields.add(obj.name);
                    }case "datetime" ->{
                        ddl.append(obj.name).append(" TIMESTAMP comment '").append(obj.label).append("',");
                        listFields.add(obj.name);
                    }case "date" -> {
                        ddl.append(obj.name).append(" date comment '").append(obj.label).append("',");
                        listFields.add(obj.name);
                    }case "time" -> {
                        ddl.append(obj.name).append(" time comment '").append(obj.label).append("',");
                        listFields.add(obj.name);
                    }case "double", "percent", "currency" -> {
                        ddl.append(obj.name).append(" double precision comment '").append(obj.label).append("',");
                        listFields.add(obj.name);
                    }case "int" -> {
                        ddl.append(obj.name).append(" int comment '").append(obj.label).append("',");
                        listFields.add(obj.name);
                    }
                }
            }

            ddl.deleteCharAt(ddl.length() - 1);
            ddl.append("); ");

        }catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        // 테이블 만들기
        etlRepository.setFieldDef(ddl.toString());

        // 데이터 쿼리 및 동적 Insert into 구성용
        StringBuilder soql = new StringBuilder();

        for (String listField : listFields) {
            soql.append(listField).append(",");
        }

        soql.deleteCharAt(soql.length() - 1);

        String query = "SELECT Id, " + soql + " FROM " + selectedObject;

        request = new Request.Builder()
                .url(INSTANCE_URL + "/services/data/v61.0/query/?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8))
                .addHeader("Authorization", "Bearer " + SalesforceOAuth.getAccessToken())
                .addHeader("Content-Type", "application/json")
                .build();

        try(Response response = client.newCall(request).execute()) {

            rootNode = objectMapper.readTree(Objects.requireNonNull(response.body()).string());
            JsonNode records = rootNode.get("records");

            StringBuilder upperQuery = new StringBuilder();
            upperQuery.append("Insert Into config.").append(selectedObject).append("(sfid, ").append(soql).append(") ").append("values");

            // 객체로 넣을려면 dto가 필요한데. 문자열로 풀어서 넣는것 말고 다른 방법 없을까.

            List<String> listUnderQuery = new ArrayList<>();

            StringBuilder underQuery;
            for (JsonNode record : records) {

                underQuery = new StringBuilder();
                underQuery.append("(").append(record.get("Id")).append(",");

                for (String field : listFields) {

                    if(mapType.get(field).equals("datetime")){
                        underQuery.append(record.get(field).toString().replace(".000+0000","").replace("T"," ")).append(",");
                    }else if(mapType.get(field).equals("time")){
                        underQuery.append(record.get(field).toString().replace("Z","")).append(",");
                    }else{
                        underQuery.append(record.get(field)).append(",");
                    }

                }

                underQuery.deleteCharAt(underQuery.length() - 1);
                underQuery.append(")");

                listUnderQuery.add(String.valueOf(underQuery));
            }

            Instant start = Instant.now();

            int insertedData = etlRepository.insertObject(upperQuery.toString(), listUnderQuery);

            Instant end = Instant.now();
            Duration interval = Duration.between(start, end);

            long hours = interval.toHours();
            long minutes = interval.toMinutesPart();
            long seconds = interval.toSecondsPart();

            System.out.println("테이블 : " + selectedObject + ". 삽입된 데이터 수 : " + insertedData + ". 소요시간 : " + hours + "시간 " + minutes + "분 " + seconds + "초");

        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}