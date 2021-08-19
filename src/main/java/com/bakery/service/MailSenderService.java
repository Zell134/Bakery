package com.bakery.service;

import com.bakery.feignClients.MailSenderClient;
import com.bakery.models.MailObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {


    private MailSenderClient senderClient;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Autowired
    public MailSenderService(MailSenderClient senderClient) {
        this.senderClient = senderClient;
    }

    public void send(String mailTo, String subject, String message) {
        senderClient.sendMail(new MailObject(mailTo, subject, message));
    }

}
