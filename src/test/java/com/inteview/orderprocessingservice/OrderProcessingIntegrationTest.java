package com.inteview.orderprocessingservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderProcessingIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    private static final String VALID = """
            {"customerId":"12345","product":"Laptop","quantity":1,"price":1200}""";

    private long createOrder()throws Exception{
        MvcResult result = mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }

    @Test
    void createOrder_retrun201WithCreatedStatus() throws Exception{
            mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(VALID))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.customerId").value("12345"))
                    .andExpect(jsonPath("$.product").value("Laptop"))
                    .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void createOrder_invalid_returns400WithMessages() throws Exception{
        String bad_request = """
                {"quantity":0,"product":"Laptop","price":-1}""";
        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(bad_request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.length()").value(3));
    }

    @Test
    void getOrder_returnsPersistedOrder() throws Exception{
        long id = createOrder();
        mockMvc.perform(get("/orders/{id}",id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.quantity").value(1));
    }

    @Test
    void getOrder_unknown_returns404() throws Exception{
        mockMvc.perform(get("/orders/{id}",999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id: 999999"));
    }

    @Test
    void updateStatus_ChangesStatus() throws Exception{
        long id = createOrder();
        mockMvc.perform(put("/orders/{id}/status", id).contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"SHIPPED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        mockMvc.perform(get("/orders/{id}", id))
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void updateStatus_invalidValue_returns400() throws Exception{
        long id = createOrder();
        mockMvc.perform(put("/orders/{id}/status", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"FLYING\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_includesCreatedOrders() throws Exception{
        createOrder();
        MvcResult result = mockMvc.perform(get("/orders")).andExpect(status().isOk()).andReturn();
        JsonNode arr = objectMapper.readTree(result.getResponse().getContentAsString());
        assertTrue(arr.isArray() && !arr.isEmpty());
    }
}
