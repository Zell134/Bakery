package com.bakery;

import com.bakery.models.User;
import static org.hamcrest.CoreMatchers.containsString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(value = {"/sqlScripts/createUserBefore.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sqlScripts/createUserAfter.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CoreControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Value("${bakery.title}")
    private String title;

    @Test
    public void indexShouldReturnBakeryName() throws Exception {
        mockMvc
                .perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(title)));
    }

    @Test
    public void indexShouldReturnContactsPage() throws Exception {
        mockMvc
                .perform(get("/contacts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(title)));
    }

    @Test
    @WithUserDetails("mailzell@inbox.ru")
    public void indexShouldReturnProfilePage() throws Exception {
        mockMvc
                .perform(get("/profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(xpath("//h1[@id='name']").string(containsString("Профиль пользователя")))
                .andExpect(xpath("//input[@name='username'][@value='admin']").exists())
                .andExpect(xpath("//input[@name='street'][@value='street']").exists())
                .andExpect(xpath("//input[@name='house'][@value='1']").exists())
                .andExpect(xpath("//input[@name='apartment'][@value='1']").exists())
                .andExpect(xpath("//input[@name='phone'][@value='12345678912']").exists())
                .andExpect(xpath("//input[@name='email'][@value='mailzell@inbox.ru']").exists());
    }

    @Test
    public void indexShouldReturnLoginPage() throws Exception {
        mockMvc
                .perform(get("/profile"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails("mailzell@inbox.ru")
    public void shouldChangeAllAuthenicatedData() throws Exception {
        User user = new User();
        user.setUsername("user");
        user.setStreet("anotherStreet");
        user.setHouse("123");
        user.setApartment(456);
        user.setPhone("987654321");
        mockMvc.perform(post("/profile")
                .param("username", "user")
                .param("street", "anotherStreet")
                .param("house", "123")
                .param("apartment", "456")
                .param("phone", "987654321")
                .param("email", "mailzell@inbox.ru")
                .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/catalog"));
        
        
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(xpath("//h1[@id='name']").string(containsString("Профиль пользователя")))
                .andExpect(xpath("//input[@name='username'][@value='user']").exists())
                .andExpect(xpath("//input[@name='street'][@value='anotherStreet']").exists())
                .andExpect(xpath("//input[@name='house'][@value='123']").exists())
                .andExpect(xpath("//input[@name='apartment'][@value='456']").exists())
                .andExpect(xpath("//input[@name='phone'][@value='987654321']").exists())
                .andExpect(xpath("//input[@name='email'][@value='mailzell@inbox.ru']").exists());
    }
}
