package com.owlearn.service;

import com.owlearn.dto.request.RecommendRequestDto;
import com.owlearn.dto.response.RecommendResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final RestTemplate restTemplate;

    private final String FastAPIUrl = "http://localhost:8000/ai/chatbot";

    public List<RecommendResponseDto> recommend(RecommendRequestDto req) {

        try {
            ResponseEntity<List<RecommendResponseDto>> resp = restTemplate.exchange(
                    URI.create(FastAPIUrl),
                    HttpMethod.POST,
                    new HttpEntity<>(req, defaultJsonHeaders()),
                    new ParameterizedTypeReference<List<RecommendResponseDto>>() {}
            );

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                return resp.getBody();
            } else {
                return Collections.emptyList();
            }
        } catch (RestClientException ex) {
            return Collections.emptyList();
        }
    }

    private HttpHeaders defaultJsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setAccept(List.of(MediaType.APPLICATION_JSON));
        return h;
    }
}
