package dev.artur.deliveryfeeservice.repository;

import dev.artur.deliveryfeeservice.model.WeatherStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static dev.artur.deliveryfeeservice.model.City.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class WeatherStationRepositoryIntTest {

    @Autowired
    private WeatherStationRepository repository;

    @BeforeEach
    public void populateRepository() {
        repository.save(new WeatherStation(TALLINN, "26038", -0.6, 2.7, "Cloudy with clear spells", 1680168273L));
        repository.save(new WeatherStation(TALLINN, "26038", 0.2, 3.6, "Cloudy with clear spells", 1680171979L));
        repository.save(new WeatherStation(TALLINN, "26038", 0.0, 2.5, "Overcast", 1680182586L));
        repository.save(new WeatherStation(TARTU, "26242", -2.1, 3.5, "Light snowfall", 1680168273L));
        repository.save(new WeatherStation(TARTU, "26242", -1.7, 3.2, "Light snowfall", 1680171979L));
        repository.save(new WeatherStation(TARTU, "26242", -0.5, 2.2, "Light shower", 1680182586L));
        repository.save(new WeatherStation(PARNU, "41803", -1.4, 4.0, "", 1680168273L));
        repository.save(new WeatherStation(PARNU, "41803", -0.9, 3.9, "", 1680171979L));
        repository.save(new WeatherStation(PARNU, "41803", -0.2, 2.8, "", 1680182586L));
    }

    @Test
    public void testLatestWeatherDataRetrieved() {
        //Assert that data is present and that the latest data is returned for each city
        repository.findAll(Sort.by(Sort.Direction.DESC, "timestamp")).forEach(System.out::println);
        Optional<WeatherStation> weatherStationTLN = repository.findRecentByCity(TALLINN);
        assertTrue(weatherStationTLN.isPresent());
        assertEquals(TALLINN, weatherStationTLN.get().getCity());
        assertEquals(1680182586L, weatherStationTLN.get().getTimestamp());
        Optional<WeatherStation> weatherStationTRT = repository.findRecentByCity(TARTU);
        assertTrue(weatherStationTRT.isPresent());
        assertEquals(TARTU, weatherStationTRT.get().getCity());
        assertEquals(1680182586L, weatherStationTRT.get().getTimestamp());
        Optional<WeatherStation> weatherStationPRN = repository.findRecentByCity(PARNU);
        assertTrue(weatherStationPRN.isPresent());
        assertEquals(PARNU, weatherStationPRN.get().getCity());
        assertEquals(1680182586L, weatherStationPRN.get().getTimestamp());
    }
}
