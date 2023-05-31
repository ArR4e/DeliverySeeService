package dev.artur.deliveryfeeservice.service;

import dev.artur.deliveryfeeservice.model.WeatherStation;
import dev.artur.deliveryfeeservice.repository.WeatherStationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class WeatherStationServiceIntTest {
    @MockBean
    private WeatherStationRepository repository;

    @Test
    void testSheduledFetch() throws InterruptedException {
        //Cron expression in test environment is set to run at 12-seconds interval
        sleep(20000);
        //CronJob should have been executed at leas once
        verify(repository, atLeast(3)).save(any(WeatherStation.class));
    }
}
