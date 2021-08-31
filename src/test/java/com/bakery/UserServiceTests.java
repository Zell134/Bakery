package com.bakery;

import com.bakery.data.UserRepository;
import com.bakery.models.RegistrationForm;
import com.bakery.models.Role;
import com.bakery.models.User;
import com.bakery.services.MailSender;
import com.bakery.services.UserService;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@ActiveProfiles("test")
@Sql(value = {"/sqlScripts/createUserAfter.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserServiceTests {

    @Autowired
    private UserService userService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private MailSender mailSender;
    @MockBean
    private UserRepository userRepo;
    
    @Test
    public void addUser() {
        RegistrationForm user = new RegistrationForm();
        user.setApartment(1);
        user.setEmail("mail@mail.ru");
        user.setHouse("1");
        user.setPassword("1234");
        user.setPhone("123456789");
        user.setStreet("street");
        user.setUsername("userName");
        User createdUser = userService.addUser(user, "localhost");

        assertNotNull(createdUser);
        assertNotNull(createdUser.getActivationCode());
        assertTrue(CoreMatchers.is(createdUser.getRoles()).matches(Collections.singleton(Role.USER)));

        Mockito.verify(userRepo, Mockito.times(1)).save(createdUser);
        Mockito.verify(mailSender, Mockito.times(1)).send(
                ArgumentMatchers.matches(user.getEmail()),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString());

    }

    @Test
    public void addUserFailTest() {
        Mockito.doReturn(new User())
                .when(userRepo)
                .findByEmailIgnoreCase("mail@mail.ru");

        RegistrationForm user = new RegistrationForm();
        user.setApartment(1);
        user.setEmail("mail@mail.ru");
        user.setHouse("1");
        user.setPassword("1234");
        user.setPhone("123456789");
        user.setStreet("street");
        user.setUsername("userName");
        User createdUser = userService.addUser(user, "localhost");

        assertNull(createdUser);

        Mockito.verify(userRepo, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
        Mockito.verify(mailSender, Mockito.times(0)).send(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString());
    }

    @Test
    public void activateUserTest() {

        User user = new User();
        user.setActivationCode("anyActivationCode");
        Mockito.doReturn(user)
                .when(userRepo)
                .findByActivationCode("anyActivationCode");

        boolean isActivate = userService.activateUser("anyActivationCode");
        assertTrue(isActivate);
        assertNull(user.getActivationCode());

        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }

    @Test
    public void activateUserFailTest() {
        boolean isActivate = userService.activateUser("anyActivationCode");
        assertFalse(isActivate);

        Mockito.verify(userRepo, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
    }
}
