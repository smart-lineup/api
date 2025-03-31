package com.jun.smartlineup.utils;

import com.jun.smartlineup.exception.TossApiException;
import com.jun.smartlineup.payment.dto.ApiResult;
import com.jun.smartlineup.payment.dto.TossFailDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebUtil {
    public static <T> ApiResult<T> postTossWithJson(String url, String secretKey, Object requestBody, Class<T> responseType) {
        WebClient webClient = WebClient.create();

        try {
            T data = webClient.post()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Basic " + secretKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            response.bodyToMono(TossFailDto.class)
                                    .flatMap(err -> Mono.error(new TossApiException(err)))
                    )
                    .bodyToMono(responseType)
                    .block();

            return ApiResult.success(data);
        } catch (TossApiException ex) {
            return ApiResult.failure(ex.getError());
        }
    }
}
