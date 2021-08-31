package com.bakery;

import com.bakery.data.ProductionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = {"/sqlScripts/createProdictionBefore.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sqlScripts/createProductionAfter.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CatalogControllerTests {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ProductionRepository repo;

    @Test
    public void shouldReturnListOfTypes() throws Exception {
        mockMvc.perform(get("/catalog"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("/html/body/div[2]/div[1]/ul/div").nodeCount(3));

    }

    @Test
    public void shouldReturnAllCatalog() throws Exception {
        mockMvc.perform(get("/catalog"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//div[@class='row row-cols-1 row-cols-md-3 g-4']").nodeCount(2))
                .andExpect(xpath("(//div[@class='row row-cols-1 row-cols-md-3 g-4'])[1]/*/div[@class='col']").nodeCount(3))
                .andExpect(xpath("(//div[@class='row row-cols-1 row-cols-md-3 g-4'])[2]/*/div[@class='col']").nodeCount(4));
    }
    
    @Test 
    public void shouldReturnProductInfoPage() throws Exception{
        
        mockMvc.perform(get("/catalog/info/" + repo.findByName("name1").getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//h2[@class='text-left']").string("name1"));
    }
    
    @Test
    public void shouldReturnOntyFirstTypeProduction() throws Exception{
        mockMvc.perform(get("/catalog/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//div[@class='row row-cols-1 row-cols-md-3 g-4']").nodeCount(1))
                .andExpect(xpath("//div[@class='col']").nodeCount(3));
    }
    
    @Test
    public void shouldReturnOntySecondTypeProduction() throws Exception{
        mockMvc.perform(get("/catalog/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(xpath("//div[@class='row row-cols-1 row-cols-md-3 g-4']").nodeCount(1))
                .andExpect(xpath("//div[@class='col']").nodeCount(4));
    }

}
