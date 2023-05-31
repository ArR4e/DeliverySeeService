package dev.artur.deliveryfeeservice.service;

import dev.artur.deliveryfeeservice.exception.ForbiddenVehicleUsageException;
import dev.artur.deliveryfeeservice.model.WeatherStation;
import dev.artur.deliveryfeeservice.repository.WeatherStationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static dev.artur.deliveryfeeservice.model.City.*;
import static dev.artur.deliveryfeeservice.model.Vehicle.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeliveryFeeServiceUnitTest {
    private DeliveryFeeService service;
    @Mock
    private WeatherStationRepository repository;
    private static final BigDecimal ZERO = new BigDecimal("0.0");
    private static final BigDecimal ONE = new BigDecimal("1.0");

    @Test
    public void testCalculateRBF() {
        Assertions.assertEquals(new BigDecimal("4.0"), DeliveryFeeService.calculateRBF(TALLINN, CAR));
        Assertions.assertEquals(new BigDecimal("3.5"), DeliveryFeeService.calculateRBF(TALLINN, SCOOTER));
        Assertions.assertEquals(new BigDecimal("3.0"), DeliveryFeeService.calculateRBF(TALLINN, BIKE));

        Assertions.assertEquals(new BigDecimal("3.5"), DeliveryFeeService.calculateRBF(TARTU, CAR));
        Assertions.assertEquals(new BigDecimal("3.0"), DeliveryFeeService.calculateRBF(TARTU, SCOOTER));
        Assertions.assertEquals(new BigDecimal("2.5"), DeliveryFeeService.calculateRBF(TARTU, BIKE));

        Assertions.assertEquals(new BigDecimal("3.0"), DeliveryFeeService.calculateRBF(PARNU, CAR));
        Assertions.assertEquals(new BigDecimal("2.5"), DeliveryFeeService.calculateRBF(PARNU, SCOOTER));
        Assertions.assertEquals(new BigDecimal("2.0"), DeliveryFeeService.calculateRBF(PARNU, BIKE));
    }

    @Test
    public void testCalculateATEF() {
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateATEF(10, CAR));
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateATEF(10, SCOOTER));
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateATEF(10, BIKE));

        Assertions.assertEquals(ONE, DeliveryFeeService.calculateATEF(-11, BIKE));
        Assertions.assertEquals(ONE, DeliveryFeeService.calculateATEF(-11, SCOOTER));
        //Controll that ATEF is not calculated for car as well
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateATEF(-11, CAR));

        Assertions.assertEquals(new BigDecimal("0.5"), DeliveryFeeService.calculateATEF(-5, BIKE));
        Assertions.assertEquals(new BigDecimal("0.5"), DeliveryFeeService.calculateATEF(-5, SCOOTER));
        //Controll that ATEF is not calculated for car as well
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateATEF(-5, CAR));
    }

    @Test
    public void testCalculateWPEF() {
        Assertions.assertEquals(ONE, DeliveryFeeService.calculateWPEF("Light snowfall", BIKE));
        Assertions.assertEquals(ONE, DeliveryFeeService.calculateWPEF("Light snowfall", SCOOTER));
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateWPEF("Light snowfall", CAR));

        Assertions.assertEquals(new BigDecimal("0.5"), DeliveryFeeService.calculateWPEF("Light rain", BIKE));
        Assertions.assertEquals(new BigDecimal("0.5"), DeliveryFeeService.calculateWPEF("Light rain", SCOOTER));
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateWPEF("Light rain", CAR));

        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateWPEF("Glaze", CAR));

        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateWPEF("Clear", BIKE));
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateWPEF("Clear", SCOOTER));
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateWPEF("Clear", CAR));
    }

    @Test
    public void testCalculateWSEF() {
        Assertions.assertEquals(new BigDecimal("0.5"), DeliveryFeeService.calculateWSEF(15, BIKE));
        //Other ranges and vehicles do not get extra fee
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateWSEF(15, SCOOTER));
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateWSEF(15, CAR));
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateWSEF(9, BIKE));
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateWSEF(9, SCOOTER));
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateWSEF(9, CAR));
        //Exception is not thrown for scooter and car
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateWSEF(21, SCOOTER));
        Assertions.assertEquals(ZERO, DeliveryFeeService.calculateWSEF(21, CAR));
    }

    @Test
    public void testWPEFThrows() {
        assertThrows(ForbiddenVehicleUsageException.class, () -> DeliveryFeeService.calculateWPEF("Hail", BIKE));
        assertThrows(ForbiddenVehicleUsageException.class, () -> DeliveryFeeService.calculateWPEF("Thunder", BIKE));
        assertThrows(ForbiddenVehicleUsageException.class, () -> DeliveryFeeService.calculateWPEF("Glaze", BIKE));
        assertThrows(ForbiddenVehicleUsageException.class, () -> DeliveryFeeService.calculateWPEF("Hail", SCOOTER));
        assertThrows(ForbiddenVehicleUsageException.class, () -> DeliveryFeeService.calculateWPEF("Thunder", SCOOTER));
        assertThrows(ForbiddenVehicleUsageException.class, () -> DeliveryFeeService.calculateWPEF("Glaze", SCOOTER));

    }

    @Test
    public void testWSEFThrows() {
        assertThrows(ForbiddenVehicleUsageException.class, () -> DeliveryFeeService.calculateWSEF(20.1, BIKE));
    }

    @Test
    public void testCalculateFee() {
        this.service = new DeliveryFeeService(repository);
        when(repository.findRecentByCity(any()))
                .thenReturn(Optional.of(new WeatherStation(TARTU, "", -2.1, 4.7, "Light snow shower")));
        assertEquals(new BigDecimal("4.0"), service.calculateDeliveryFee(TARTU, BIKE));
    }

    @Test
    public void testCalculateFeeThrows() {
        this.service = new DeliveryFeeService(repository);
        when(repository.findRecentByCity(any()))
                .thenReturn(Optional.of(new WeatherStation(TARTU, "", -2.1, 20.1, "Hail")));
        assertThrows(ForbiddenVehicleUsageException.class, () -> service.calculateDeliveryFee(TARTU, SCOOTER));
        assertThrows(ForbiddenVehicleUsageException.class, () -> service.calculateDeliveryFee(TALLINN, BIKE));
    }
}
