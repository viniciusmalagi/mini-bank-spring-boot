package com.vmlg.bank.bank.services;

import org.springframework.stereotype.Service;

import com.vmlg.bank.bank.domain.user.User;

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
