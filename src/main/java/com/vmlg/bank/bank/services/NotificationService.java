package com.vmlg.bank.bank.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vmlg.bank.bank.domain.user.User;
import com.vmlg.bank.bank.dtos.NotificationDTO;

@Service
public class NotificationService {
    // @Autowired
    // private RestTemplate restTemplate;

    public void sendNotification(User user, String message) throws Exception{
        String email = user.getEmail();
        // TODO Notification
        // NotificationDTO notificationDTO = new NotificationDTO(email, message);
        // ResponseEntity<String> notificationResponse = restTemplate.postForEntity("url", notificationDTO, String.class);
        // if (!(notificationResponse.getStatusCode() == HttpStatus.OK)) {
            // throw new Exception("Notification service unavailable");
        // }
        System.out.println(email);
        System.out.println(message);
    }
}
