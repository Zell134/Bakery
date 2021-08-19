package com.bakery.service;

import com.bakery.feignClients.UserServiceClient;
import com.bakery.models.User;
import com.bakery.models.RegistrationForm;
import com.bakery.models.UserForDeserialozation;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private UserServiceClient userClient;
    private HttpServletRequest request;

    @Autowired
    public UserService(UserServiceClient userClient, HttpServletRequest request) {
        this.userClient = userClient;
        this.request = request;
    }

    @Override
    public UserDetails loadUserByUsername(String email){
        UserForDeserialozation user = userClient.loadUserByUsername(email);
                if (user != null) {
            request.getSession().setAttribute("user", user.toUser());
            return user.toUser();
        }
        throw new UsernameNotFoundException("User '" + email + "' not found");
    }

    public User addUser(User user, String host) {
        return userClient.addUser(user, host).toUser();
    }

    public boolean activateUser(String code) {
        return userClient.activateUser(code);
    }

    public void uppdateUser(String email, User changedUser) {

        UserForDeserialozation deserializedUser = userClient.updateUser(email, changedUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(deserializedUser.toUser(), 
                deserializedUser.getPassword(), 
                deserializedUser.toUser().getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public boolean remindPassword(String email) {
        return userClient.remindPassword(email);
    }

    public void changePassword(User user, String password) {
        userClient.changePassword(user.getEmail(), password);
    }

}
