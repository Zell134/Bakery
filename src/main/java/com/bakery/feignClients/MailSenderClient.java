package com.bakery.feignClients;

import com.bakery.models.MailObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mail-sender")
public interface MailSenderClient {
    
    @PostMapping("/send")
    public void sendMail(@RequestBody MailObject mail);
    
}
