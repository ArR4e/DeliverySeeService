package dev.artur.deliveryfeeservice;

import dev.artur.deliveryfeeservice.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static dev.artur.deliveryfeeservice.model.City.*;
import static dev.artur.deliveryfeeservice.model.Vehicle.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeliveryFeeServiceApplicationE2ETest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testApplication() throws IOException, URISyntaxException, InterruptedException {
        Path path = Path.of(DeliveryFeeServiceApplication.class.getClassLoader().getResource("mock-data.xml").toURI());
        String mockResponseBody = Files.readString(path);
        when(restTemplate.getForEntity(any(String.class), any()))
                .thenReturn(ResponseEntity.ok(mockResponseBody));
        Thread.sleep(20000);
        String url = "/api/v1/delivery/calculate-fee?city={city}&vehicle={vehicle}";

        Map<String, Enum<?>> params = Map.of("city", TALLINN, "vehicle", CAR);
        ResponseEntity<BigDecimal> response = testRestTemplate.getForEntity(url, BigDecimal.class, params);
        assertEquals(OK, response.getStatusCode());
        assertEquals(new BigDecimal("4.0"), response.getBody());

        params = Map.of("city", TALLINN, "vehicle", SCOOTER);
        ResponseEntity<ErrorMessage> errorResponse = testRestTemplate.getForEntity(url, ErrorMessage.class, params);
        assertEquals(UNPROCESSABLE_ENTITY, errorResponse.getStatusCode());
        assertEquals("Usage of selected vehicle type is forbidden", errorResponse.getBody().getMessage());

        params = Map.of("city", TALLINN, "vehicle", BIKE);
        errorResponse = testRestTemplate.getForEntity(url, ErrorMessage.class, params);
        assertEquals(UNPROCESSABLE_ENTITY, errorResponse.getStatusCode());
        assertEquals("Usage of selected vehicle type is forbidden", errorResponse.getBody().getMessage());

        params = Map.of("city", TARTU, "vehicle", CAR);
        response = testRestTemplate.getForEntity(url, BigDecimal.class, params);
        assertEquals(OK, response.getStatusCode());
        assertEquals(new BigDecimal("3.5"), response.getBody());

        params = Map.of("city", TARTU, "vehicle", SCOOTER);
        response = testRestTemplate.getForEntity(url, BigDecimal.class, params);
        assertEquals(OK, response.getStatusCode());
        assertEquals(new BigDecimal("4.5"), response.getBody());

        params = Map.of("city", TARTU, "vehicle", BIKE);
        response = testRestTemplate.getForEntity(url, BigDecimal.class, params);
        assertEquals(OK, response.getStatusCode());
        assertEquals(new BigDecimal("4.0"), response.getBody());

        params = Map.of("city", PARNU, "vehicle", CAR);
        response = testRestTemplate.getForEntity(url, BigDecimal.class, params);
        assertEquals(OK, response.getStatusCode());
        assertEquals(new BigDecimal("3.0"), response.getBody());

        params = Map.of("city", PARNU, "vehicle", SCOOTER);
        response = testRestTemplate.getForEntity(url, BigDecimal.class, params);
        assertEquals(OK, response.getStatusCode());
        assertEquals(new BigDecimal("4.5"), response.getBody());

        params = Map.of("city", PARNU, "vehicle", BIKE);
        errorResponse = testRestTemplate.getForEntity(url, ErrorMessage.class, params);
        assertEquals(UNPROCESSABLE_ENTITY, errorResponse.getStatusCode());
        assertEquals("Usage of selected vehicle type is forbidden", errorResponse.getBody().getMessage());
    }
}
