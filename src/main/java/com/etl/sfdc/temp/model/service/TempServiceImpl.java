package com.etl.sfdc.temp.model.service;

import com.etl.sfdc.temp.model.dto.TempMember;
import com.etl.sfdc.temp.model.repository.TempRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TempServiceImpl implements TempService{

    public long tryHttpUrlConnection(String uri)  {
        String endpoint = "http://www.hsj-nas.kr:3927/temp/" + uri;
        List<TempMember> listTempMember = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        try {
            URL url = new URL(endpoint);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 메서드
            connection.setRequestMethod("GET");

            // ConnectTimeout
            connection.setConnectTimeout(10000);

            // ReadTimeout
            connection.setReadTimeout(10000);

            int statusCode = connection.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String resBody;
                StringBuilder sb = new StringBuilder();

                while ((resBody = br.readLine()) != null) {
                    sb.append(resBody);
                }
                br.close();

                ObjectMapper objectMapper = new ObjectMapper();
                listTempMember = List.of(objectMapper.readValue(sb.toString(), TempMember[].class));

                System.out.println("인입된 변수 : " + uri);

            }else{
                System.out.println("응답이 이상해");
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        long endTime = System.currentTimeMillis();

        return endTime - startTime;

    }

    @Override
    public long tryWebClient(String uri) {

        long startTime = System.currentTimeMillis();

        WebClient client = WebClient
                .builder()
                .baseUrl("http://www.hsj-nas.kr:3927/temp/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Mono<String> responseMono = client
                .get()
                .uri(uri)
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(10));
                })
                .retrieve()
                .bodyToMono(String.class);

        responseMono.subscribe(
            response -> {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    List<TempMember> listTempMember = List.of(objectMapper.readValue(response, TempMember[].class));
                    System.out.println("인입된 변수 : " + uri);
                } catch (JsonProcessingException e) {
                    System.out.println("응답이 이상해");
                }
            },
            error -> {
                System.err.println("Error: " + error.getMessage());
            },
            () ->{
                System.err.println("Null 값이 날라옴");
            }
        );

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    @Override
    public void patchCometD(String topicNm) {

        Map<String, String> mapTopic = new HashMap<>();
        mapTopic.put("topicNm",topicNm);

        WebClient client = WebClient
                .builder()
                .baseUrl("http://127.0.0.1:3940/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        Mono<String> responseMono = client
                .post()
                .uri("cometd/hand-shake")
                .bodyValue(mapTopic)
                .httpRequest(httpRequest -> {
                    HttpClientRequest reactorRequest = httpRequest.getNativeRequest();
                    reactorRequest.responseTimeout(Duration.ofSeconds(3));
                })
                .retrieve()
                .bodyToMono(String.class);

        responseMono.subscribe(response -> {
            try {
                System.out.println(response);
            } catch (Exception e) {
                System.out.println("응답이 이상해");
            }
        },
        error -> {
            System.err.println("Error: " + error.getMessage());
        });
    }
}
