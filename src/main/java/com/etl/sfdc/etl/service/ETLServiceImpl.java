package com.etl.sfdc.etl.service;

import com.etl.sfdc.common.SalesforceOAuth;
import com.etl.sfdc.etl.dto.ObjectDefinition;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ETLServiceImpl implements ETLService {

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

        ObjectMapper objectMapper = new ObjectMapper();

        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

        // Map 만들고 잭슨으로 직렬화
        String json = objectMapper.writeValueAsString(getProprtyMap(selectedObject));

        RequestBody formBody = RequestBody.create(
                json, MediaType.get("application/json; charset=utf-8")
        );

        // x-www-form-urlencoded 말고 얌전히 json 보내자
        Request request = new Request.Builder()
                .url("http://127.0.0.1:3931/pubsub")
                .post(formBody)
                .addHeader("Content-Type", "application/json")
                .build();

        try(Response response = client.newCall(request).execute()) {

            System.out.println(Objects.requireNonNull(response.body()).string());

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static @NotNull Map<String, String> getProprtyMap(String selectedObject) {
        Map<String, String> mapProperty = new HashMap<>();
        mapProperty.put("loginUrl","https://login.salesforce.com");
        mapProperty.put("client_id","3MVG9q4K8Dm94dAwF6D70zsfWDZO2vEz0CCf0bQtywOlbgdghIYL0JLpyG2HP5bvUkV5Lm1B.4bZE0z6pkVu3");
        mapProperty.put("client_secret","A532570413C1AB3952E7CEDEBDBFF0735651F2F8011B093C8CE03A646FF9AD0A");
        mapProperty.put("username","admin@ecologysyncmanagementco.kr.dev");
        mapProperty.put("password","qwer1234!t9IOoeW2u0GeELmPoVh4BOmh");
        mapProperty.put("instanceUrl","https://ecologysyncmanagement-dev-ed.develop.my.salesforce.com");
        mapProperty.put("selectedObject", selectedObject);

        return mapProperty;
    }
}