package dev.artur.deliveryfeeservice.controller;

import dev.artur.deliveryfeeservice.model.City;
import dev.artur.deliveryfeeservice.service.DeliveryFeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dev.artur.deliveryfeeservice.model.Vehicle;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/delivery")
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }


    @GetMapping(value = "/calculate-fee", produces = "application/json")
    public ResponseEntity<BigDecimal> calculate(@RequestParam City city, @RequestParam Vehicle vehicle) {
        return ResponseEntity.ok(deliveryFeeService.calculateDeliveryFee(city, vehicle));
    }

}
