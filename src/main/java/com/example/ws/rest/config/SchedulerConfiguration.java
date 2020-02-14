package com.example.ws.rest.config;

import com.example.ws.rest.domain.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.UUID;

import static com.example.ws.rest.config.BeanConfiguration.collectionWsSessionIdBean;

@EnableScheduling
@Configuration
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class SchedulerConfiguration {

    private final SimpMessagingTemplate template;

    @Setter(onMethod_ = {@Autowired, @Qualifier(collectionWsSessionIdBean)})
    private Collection<String> collectionWsSessionId;


    @Scheduled(fixedDelay = 3000)
    public void sendAdhocMessages() {
        collectionWsSessionId
                .forEach(user ->
                        send(user, Destination.FINAL, new UserResponse(UUID.randomUUID().toString()))
                );
    }

    private void send(@NotNull String user, String destination, Object object) {
        template.convertAndSendToUser(user, destination, object);
    }

    private void send(String destination, Object object) {
        template.convertAndSend(destination, object);
    }
}
