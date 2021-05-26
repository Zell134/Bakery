package com.bakery.service;

import com.bakery.data.UserRepository;
import com.bakery.models.User;
import com.bakery.models.RegistrationForm;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MailSender mailSender;
    @Value("${bakery.host}")
    String host;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email);
        if (user != null) {
            request.getSession().setAttribute("user", user);
            return user;
        }
        throw new UsernameNotFoundException("User '" + email + "' not found");
    }

    public User addUser(RegistrationForm form) {
        User user = userRepository.findByEmailIgnoreCase(form.getEmail());
        if (user != null) {
            return null;
        }
        user = form.toUser(passwordEncoder);
        user.setActive(false);
        user.setActivationCode(UUID.randomUUID().toString());
        userRepository.save(user);

        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Здравствуйте, %s! \n Для активации аккаунта пройдите, пожалуйста по ссылке: %s/registration/activate/%s",
                    user.getUsername(),
                    host,
                    user.getActivationCode());
            mailSender.send(user.getEmail(), "Activation code", message);
        }

        return user;
    }

    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);
        if (user == null) {
            return false;
        }
        user.setActivationCode(null);
        user.setActive(true);

        userRepository.save(user);
        

        return true;
    }

    public void uppdateUser(User user, User changedUser) {
        
        user.setUsername(changedUser.getUsername());
        user.setStreet(changedUser.getStreet());
        user.setHouse(changedUser.getHouse());
        user.setApartment(changedUser.getApartment());
        user.setPhone(changedUser.getPhone());
        
        userRepository.save(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
