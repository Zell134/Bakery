package com.bakery.security;

import com.bakery.data.UserRepository;
import com.bakery.models.User;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserRepositoryUserDetailService implements UserDetailsService {

    private final UserRepository userRepository; 
    private final HttpServletRequest request;
    
    public UserRepositoryUserDetailService(UserRepository userRepository, HttpServletRequest request) {
        this.userRepository = userRepository;
        this.request = request;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            request.getSession().setAttribute("user", user);
            return user;
        }
        throw new UsernameNotFoundException("User '" + email + "' not found");
    }
}
