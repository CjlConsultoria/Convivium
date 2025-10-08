package br.com.convivium.controller;

import br.com.convivium.dto.request.ChatRequest;
import br.com.convivium.dto.response.ChatResponse;
import br.com.convivium.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.processMessage(request);
    }
}
