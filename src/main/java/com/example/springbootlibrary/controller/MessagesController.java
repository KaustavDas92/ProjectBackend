package com.example.springbootlibrary.controller;

import com.example.springbootlibrary.entity.Message;
import com.example.springbootlibrary.requestModels.AdminQuestionRequest;
import com.example.springbootlibrary.service.MessagesService;
import com.example.springbootlibrary.utils.ExtractJWTToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/messages")
public class MessagesController {

    public MessagesService messagesService;

    @Autowired
    public MessagesController(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @PostMapping("/secure/add/message")
    public void postMessage( @RequestHeader(value = "Authorization") String token, @RequestBody Message messageRequest){
        String userEmail= ExtractJWTToken.payloadExtract(token,"\"sub\"");
        messagesService.postMessage(messageRequest,userEmail);
    }

    @PutMapping("secure/admin/message")
    public void putMessage(@RequestHeader(value = "Authorization") String token, @RequestBody AdminQuestionRequest adminQuestionRequest) throws Exception{
        String userEmail= ExtractJWTToken.payloadExtract(token,"\"sub\"");
        String admin = ExtractJWTToken.payloadExtract(token,"\"userType\"");
        if(admin == null || !admin.equals("admin")){
            throw new Exception("Administration page only");
        }
        messagesService.putMessage(adminQuestionRequest,userEmail);
    }
}
