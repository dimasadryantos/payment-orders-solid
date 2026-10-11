package com.dimas.payments.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Starts the whole application, so this also proves the Spring wiring and the YAML policy binding. */
@SpringBootTest
@AutoConfigureMockMvc
class PaymentOrderApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void cardPaymentIsCreatedExecutedAndRefunded() throws Exception {
        String id = create("CARD", "100.00", "EUR");

        mockMvc.perform(post("/api/v1/payment-orders/{id}/execute", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SETTLED"))
                .andExpect(jsonPath("$.fee").value(1.65));

        mockMvc.perform(post("/api/v1/payment-orders/{id}/refund", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));

        mockMvc.perform(get("/api/v1/payment-orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));
    }

    @Test
    void executingTwiceIsAConflict() throws Exception {
        String id = create("SEPA_CREDIT_TRANSFER", "250.00", "EUR");

        mockMvc.perform(post("/api/v1/payment-orders/{id}/execute", id)).andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/payment-orders/{id}/execute", id)).andExpect(status().isConflict());
    }

    @Test
    void sepaPaymentCannotBeRefunded() throws Exception {
        String id = create("SEPA_CREDIT_TRANSFER", "250.00", "EUR");
        mockMvc.perform(post("/api/v1/payment-orders/{id}/execute", id)).andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/payment-orders/{id}/refund", id))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Refund not supported"));
    }

    @Test
    void walletPaymentAboveTheBalanceFails() throws Exception {
        String id = create("WALLET", "1500.00", "EUR");

        mockMvc.perform(post("/api/v1/payment-orders/{id}/execute", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.failureReason").value("Insufficient wallet balance"));
    }

    @Test
    void policyFromApplicationYmlIsEnforced() throws Exception {
        mockMvc.perform(post("/api/v1/payment-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("SEPA_CREDIT_TRANSFER", "10.00", "USD")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.violations[0]").value("SEPA_CREDIT_TRANSFER does not support USD"));

        mockMvc.perform(post("/api/v1/payment-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("CARD", "5000.01", "EUR")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void malformedRequestsAreBadRequests() throws Exception {
        mockMvc.perform(post("/api/v1/payment-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("CARD", "10.999", "EUR")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/payment-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 10.00, \"currency\": \"EUR\", \"method\": \"CARD\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownOrderIsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/payment-orders/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    private String create(String method, String amount, String currency) throws Exception {
        String response = mockMvc.perform(post("/api/v1/payment-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(method, amount, currency)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.id");
    }

    private static String body(String method, String amount, String currency) {
        return """
                {
                  "debtorAccount": "DE89370400440532013000",
                  "creditorAccount": "DE75512108001245126199",
                  "amount": %s,
                  "currency": "%s",
                  "method": "%s",
                  "reference": "Invoice 2026-001"
                }
                """.formatted(amount, currency, method);
    }
}
