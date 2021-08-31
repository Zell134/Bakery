package com.bakery;

import com.bakery.data.ProductionRepository;
import com.bakery.data.TypeRepository;
import com.bakery.models.Product;
import com.bakery.models.Type;
import java.io.File;
import java.io.FileInputStream;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = {"/sqlScripts/createProdictionBefore.sql", "/sqlScripts/createUserBefore.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sqlScripts/createProductionAfter.sql", "/sqlScripts/createUserAfter.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CatalogEditTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductionRepository productRepo;

    @Autowired
    private TypeRepository typeRepo;

    @Test
    public void shouldRedirrectToLoginPage() throws Exception {
        mockMvc.perform(get("/catalog/admin/edit"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test    
    @WithUserDetails("mailzell@inbox.ru")
    public void shouldReturnCatalogEditList() throws Exception {
        mockMvc.perform(get("/catalog/admin/edit"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(xpath("//tbody/*/tr[@class='typeName']").nodeCount(3))
                .andExpect(xpath("//tbody/*/tr[@class='table-row']").nodeCount(7))
                .andExpect(xpath("//tbody/*/tr[@class='table-row'][1]").string(StringContains.containsString("name1")));
    }

    @Test
    @WithUserDetails("mailzell@inbox.ru")
    public void shouldReturnFirstProduct() throws Exception {

        Product product = productRepo.findByName("name1");
        mockMvc.perform(get("/catalog/admin/edit/" + product.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(xpath("//input[@id='name']").string(StringContains.containsString(product.getName())))
                .andExpect(xpath("//textarea [@id='description']").string(StringContains.containsString(product.getDescription())))
                .andExpect(xpath("//input[@name='price'][@value='" + product.getPrice() + "']").exists())
                .andExpect(xpath("//input[@id='type1'][@checked='checked']").exists())
                .andExpect(xpath("//input[@id='type2']").exists());
    }

    @Test
    @WithUserDetails("mailzell@inbox.ru")
    public void shouldReturnSixProduct() throws Exception {

        Product product = productRepo.findByName("name6");

        mockMvc.perform(get("/catalog/admin/edit/" + product.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(xpath("//input[@id='name']").string(StringContains.containsString(product.getName())))
                .andExpect(xpath("//textarea [@id='description']").string(StringContains.containsString(product.getDescription())))
                .andExpect(xpath("//input[@name='price']/@value").string(StringContains.containsString(product.getPrice())))
                .andExpect(xpath("//input[@id='type1']").exists())
                .andExpect(xpath("//input[@id='type2'][@checked='checked']").exists());
    }

    @Test
    @WithUserDetails("mailzell@inbox.ru")
    public void shouldCheckActivationAndDeactivation() throws Exception {
        Product product = productRepo.findByName("name1");
        Assertions.assertEquals(true, product.isActive());
        mockMvc.perform(get("/catalog/admin/activate/" + product.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated());
        product = productRepo.findByName("name1");
        Assertions.assertEquals(false, product.isActive());
    }

    @Test
    @WithUserDetails("mailzell@inbox.ru")
    public void shouldDeleteType() throws Exception {
        Type type = typeRepo.findById(2);
        Assertions.assertNotNull(type);
        mockMvc.perform(get("/catalog/admin/types/deletType/2"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/catalog/admin/types"));
        type = typeRepo.findById(2);
        Assertions.assertNotNull(type);
        Assertions.assertEquals(type.getName(), "Неизвестный");

        type = typeRepo.findById(3);
        Assertions.assertNotNull(type);

        mockMvc.perform(get("/catalog/admin/types/deletType/3"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/catalog/admin/types"));

        type = typeRepo.findById(3);
        Assertions.assertNull(type);
    }

    @Test
    @WithUserDetails("mailzell@inbox.ru")
    public void shouldAddNewType() throws Exception {
        mockMvc.perform(post("/catalog/admin/types/addType").param("name", "newType").with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalog/admin/types"));
        mockMvc.perform(get("/catalog/admin/types"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(xpath("//tbody/*/tr").nodeCount(4));

        Type type = typeRepo.findByName("newType");
        Assertions.assertNotNull(type);
    }

    @Test
    @WithUserDetails("mailzell@inbox.ru")
    public void shouldReturnAddNewProductPage() throws Exception {
        mockMvc.perform(get("/catalog/admin/edit/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated());
    }

    @Test
    @WithUserDetails("mailzell@inbox.ru")
    public void shouldAddNewProduct() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "img.jpeg", MediaType.MULTIPART_FORM_DATA_VALUE,
                    new FileInputStream(new File("src\\main\\resources\\static\\img.jpeg")));
        
        mockMvc.perform(multipart("/catalog/admin/edit")
                            .file(file)
                            .param("imageUrl", "")
                            .param("active", "true")
                            .param("description", "newDescription")
                            .param("name", "newName")
                            .param("price", "12")
                            .param("type", "2")
                            .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection());
        Product product = productRepo.findByName("newName");
        Assertions.assertNotNull(product);
        
        File newFile = new File("src\\main\\resources\\static\\img\\" + product.getImageUrl());
        Assertions.assertEquals(true, newFile.exists());
        newFile.delete();        
    }

}
