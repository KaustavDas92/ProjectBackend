package com.example.springbootlibrary.service;

import com.example.springbootlibrary.dao.MessagesRepository;
import com.example.springbootlibrary.entity.Message;
import com.example.springbootlibrary.requestModels.AdminQuestionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MessagesService {
    private MessagesRepository messagesRepository;

    @Autowired
    public MessagesService(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    public void postMessage(Message messageRequest, String userEmail){
        Message message= new Message(messageRequest.getTitle(),messageRequest.getQuestion());
        message.setUserEmail(userEmail);
        messagesRepository.save(message);
    }

    public void putMessage (AdminQuestionRequest adminQuestionRequest,String userEmail) throws Exception{
        Optional<Message> message= messagesRepository.findById(adminQuestionRequest.getId());
        if(message.isEmpty()){
            throw new Exception("Message was not found");
        }
        message.get().setAdminEmail(userEmail);
        message.get().setResponse(adminQuestionRequest.getResponse());
        message.get().setClosed(true);
        messagesRepository.save(message.get());

    }

}
