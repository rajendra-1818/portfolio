package dev.rajendra.orderflow.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest @AutoConfigureMockMvc
class OrderControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test void createsOrderAndCalculatesTotal() throws Exception {
        var request = Map.of("customerEmail", "engineer@example.com", "items", new Object[]{
                Map.of("sku", "keyboard", "quantity", 2, "unitPrice", new BigDecimal("49.95"))});
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.total").value(99.90));
    }

    @Test void rejectsInvalidEmail() throws Exception {
        var request = Map.of("customerEmail", "invalid", "items", new Object[]{
                Map.of("sku", "keyboard", "quantity", 1, "unitPrice", 10)});
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))).andExpect(status().isBadRequest());
    }
}
