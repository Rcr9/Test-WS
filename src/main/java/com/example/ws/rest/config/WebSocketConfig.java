package com.example.ws.rest.config;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final static String WS = "/ws-sample";
    private final static String SIMP_SESSION_ID = "simpSessionId";

    @Setter(onMethod_ = {@Autowired, @Qualifier(BeanConfiguration.collectionWsSessionIdBean)})
    private Collection<String> collectionWsSessionId;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint(WS)
                .setAllowedOrigins("*")
                .setHandshakeHandler((request, response, wsHandler, attributes) -> {
                    if (request instanceof ServletServerHttpRequest) {
                        ServletServerHttpRequest servletRequest
                                = (ServletServerHttpRequest) request;
                        HttpSession session = servletRequest
                                .getServletRequest().getSession();
                        attributes.put("sessionId", session.getId());
                    }
                    return true;
                }).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/" + Destination.TYPE + "/");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @EventListener(SessionConnectedEvent.class)
    public void handleWsDisconnectListener(SessionConnectedEvent event) {
        log.info("websocket connected event, {}", event);
        getSessionIdFromEvent(event).ifPresent(collectionWsSessionId::add);
    }

    @EventListener(SessionDisconnectEvent.class)
    public void handleWsDisconnectListener(SessionDisconnectEvent event) {
        log.info("websocket disconnect event, {}", event);
        getSessionIdFromEvent(event).ifPresent(collectionWsSessionId::remove);
    }


    private Optional<String> getSessionIdFromEvent(AbstractSubProtocolEvent event) {
        String sessionId = null;
        Object sessionIdAsObject = event.getMessage().getHeaders().get(SIMP_SESSION_ID);
        if (Objects.nonNull(sessionIdAsObject) && sessionIdAsObject.getClass().equals(String.class)) {
            sessionId = (String) sessionIdAsObject;
        }
        return Optional.ofNullable(StringUtils.isEmpty(sessionId) ? null : sessionId);
    }
}
