package com.jun.smartlineup.utils;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;

public class WebUtil {
    public static <T> T postWithJson(String url, String secretKey, Object requestBody, ParameterizedTypeReference<T> responseType) {
        WebClient webClient = WebClient.create();

        return webClient.post()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + secretKey)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class).map(err ->
                                new RuntimeException("클라이언트 오류: " + err)))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .map(err -> new RuntimeException("서버 오류: " + err))
                )
                .bodyToMono(responseType)
                .block();
    }

}
