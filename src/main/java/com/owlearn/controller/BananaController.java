package com.owlearn.controller;

import com.owlearn.dto.request.BananaRequestDto;
import com.owlearn.service.BananaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/banana")
public class BananaController {

    private final BananaService service;

    public BananaController(BananaService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> generate(@RequestBody BananaRequestDto req) {
        byte[] png = service.generatePng(req.getPrompt());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"banana.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }
}