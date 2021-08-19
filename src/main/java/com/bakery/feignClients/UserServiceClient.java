package com.bakery.feignClients;

import com.bakery.models.User;
import com.bakery.models.UserForDeserialozation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserServiceClient {
    
    @GetMapping("/loadUserByUsername")
    public UserForDeserialozation loadUserByUsername(@RequestParam String email);
    
    @PostMapping("/addUser")
    public UserForDeserialozation addUser(@RequestBody User user, @RequestParam String host);
    
    @PostMapping("/activateUser")
    public boolean activateUser(@RequestParam String code);

    @PostMapping("/updateUser")
    public UserForDeserialozation updateUser(@RequestParam String email, @RequestBody User changedUser);

    @PostMapping("/remindPassword")
    public boolean remindPassword(@RequestParam String email);

    @PostMapping("/changePassword")
    public void changePassword(@RequestParam String email, @RequestBody String password);

}
