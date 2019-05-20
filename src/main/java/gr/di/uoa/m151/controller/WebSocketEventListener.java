package gr.di.uoa.m151.controller;

import gr.di.uoa.m151.entity.AppUser;
import gr.di.uoa.m151.entity.ChatMessage;
import gr.di.uoa.m151.service.RestClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

/**
 * Created by rajeevkumarsingh on 25/07/17.
 */
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private RestClientService restClientService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        AppUser sender = (AppUser) headerAccessor.getSessionAttributes().get("sender");
        if(sender != null) {
            logger.info("User Disconnected : " + sender.getFullName());

            List<ChatMessage> chatMessageList = (List<ChatMessage>) headerAccessor.getSessionAttributes().get("chatMessages");
            if(chatMessageList != null && !chatMessageList.isEmpty())
                restClientService.saveConversation(chatMessageList);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(sender);

            messagingTemplate.convertAndSend("/topic/public", chatMessage);

        }
    }
}
