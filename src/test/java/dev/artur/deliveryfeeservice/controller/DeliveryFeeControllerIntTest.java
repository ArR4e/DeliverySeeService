package dev.artur.deliveryfeeservice.controller;

import dev.artur.deliveryfeeservice.exception.ForbiddenVehicleUsageException;
import dev.artur.deliveryfeeservice.exception.NoDataAvailableException;
import dev.artur.deliveryfeeservice.service.DeliveryFeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryFeeController.class)
public class DeliveryFeeControllerIntTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DeliveryFeeService service;

    @Test
    public void testValidRequest() throws Exception {
        when(service.calculateDeliveryFee(any(), any())).thenReturn(BigDecimal.TEN);
        mockMvc.perform(
                get("/api/v1/delivery/calculate-fee")
                        .param("city", "Tallinn")
                        .param("vehicle", "Bike")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(10.0));
    }

    @Test
    public void testInvalidCity() throws Exception {
        mockMvc.perform(
                get("/api/v1/delivery/calculate-fee")
                        .param("city", "Narva")
                        .param("vehicle", "Bike")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidVehicle() throws Exception {
        mockMvc.perform(get("/api/v1/delivery/calculate-fee")
                        .param("city", "Tallinn")
                        .param("vehicle", "Airplane")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testForbiddenVehicleUsage() throws Exception {
        when(service.calculateDeliveryFee(any(), any()))
                .thenThrow(new ForbiddenVehicleUsageException("Description"));

        mockMvc.perform(
                        get("/api/v1/delivery/calculate-fee")
                                .param("city", "Tallinn")
                                .param("vehicle", "Bike")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Usage of selected vehicle type is forbidden"))
                .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    public void testNoDataAvailable() throws Exception {
        when(service.calculateDeliveryFee(any(), any()))
                .thenThrow(new NoDataAvailableException());
        mockMvc.perform(
                        get("/api/v1/delivery/calculate-fee")
                                .param("city", "Tallinn")
                                .param("vehicle", "Bike")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Currently, it is impossible to calculate fee"))
                .andExpect(jsonPath("$.description").value("The database currently has no entries for specified city, please wait for about an hour"));

    }
}

