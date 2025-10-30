package com.owlearn.controller;

import com.owlearn.dto.request.RecommendRequestDto;
import com.owlearn.dto.response.RecommendResponseDto;
import com.owlearn.service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/recommend")
    public ResponseEntity<List<RecommendResponseDto>> recommend(@RequestBody RecommendRequestDto req) {
        List<RecommendResponseDto> result = chatbotService.recommend(req);
        return ResponseEntity.ok(result);
    }
}
