package com.owlearn.controller;

import com.owlearn.dto.request.GeminiRequestDto;
import com.owlearn.service.GeminiService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    private final GeminiService service;

    public GeminiController(GeminiService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generate(@ModelAttribute GeminiRequestDto request) {
        List<String> urls = service.generateImages(request);
        return ResponseEntity.ok(urls);
    }

}