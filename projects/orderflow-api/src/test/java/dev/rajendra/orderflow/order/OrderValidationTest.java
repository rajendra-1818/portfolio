package dev.rajendra.orderflow.order;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest @AutoConfigureMockMvc
class OrderValidationTest {
    @Autowired MockMvc mvc;

    @ParameterizedTest
    @ValueSource(strings = {
        "{\"sku\":\"keyboard\",\"quantity\":1}",
        "{\"sku\":\"keyboard\",\"quantity\":0,\"unitPrice\":10}",
        "{\"sku\":\"keyboard\",\"quantity\":1,\"unitPrice\":-1}",
        "{\"sku\":\"keyboard\",\"quantity\":1,\"unitPrice\":1.111}",
        "null"
    })
    void rejectsInvalidItems(String item) throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
            .content("{\"customerEmail\":\"engineer@example.com\",\"items\":[" + item + "]}"))
            .andExpect(status().isBadRequest());
    }
}
