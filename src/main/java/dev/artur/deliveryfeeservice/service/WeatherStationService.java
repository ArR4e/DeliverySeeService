package dev.artur.deliveryfeeservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dev.artur.deliveryfeeservice.dto.ObservationsDTO;
import dev.artur.deliveryfeeservice.dto.WeatherStationDTO;
import dev.artur.deliveryfeeservice.dto.WeatherStationDTOMapper;
import dev.artur.deliveryfeeservice.model.City;
import dev.artur.deliveryfeeservice.repository.WeatherStationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Set;
import java.util.function.Predicate;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

@Service
public class WeatherStationService {
    private static final String WEATHER_API_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
    //Set of station names to filter weather stations
    private static final Set<String> NAME_FILTER = stream(City.values())
            .map(City::getStationName)
            .collect(toSet());
    private static final Predicate<WeatherStationDTO> BY_NAME =
            station -> NAME_FILTER.contains(station.getName());
    private static final Logger logger = LoggerFactory.getLogger(WeatherStationService.class);

    private final RestTemplate restTemplate;
    private final WeatherStationRepository weatherStationRepository;
    private final WeatherStationDTOMapper weatherStationDTOMapper;


    public WeatherStationService(
            WeatherStationRepository weatherStationRepository,
            WeatherStationDTOMapper weatherStationDTOMapper,
            RestTemplate restTemplate
    ) {
        this.weatherStationRepository = weatherStationRepository;
        this.weatherStationDTOMapper = weatherStationDTOMapper;
        this.restTemplate = restTemplate;
    }

    //Cron job is configurable from application.properties
    @Scheduled(cron = "${app.deliveryfee.cron.expression}")
    public void fetchWeatherData() {
        try {
            //Get response from API
            ResponseEntity<String> response = restTemplate.getForEntity(WEATHER_API_URL, String.class);
            String content = response.getBody();
            //Deserialise XML into ObservationsDTO object
            XmlMapper xmlMapper = new XmlMapper();
            ObservationsDTO observationsDTO = xmlMapper.readValue(content, ObservationsDTO.class);
            //Obtain timestamp directly from observations
            Long timestamp = observationsDTO.getTimestamp();
            //Filter only relevant stations, convert them from DTO to model,
            //set timestamp and persist
            observationsDTO.getStations().stream()
                    .filter(BY_NAME)
                    .map(weatherStationDTOMapper)
                    .peek(station -> station.setTimestamp(timestamp))
                    .forEach(weatherStationRepository::save);
        } catch (RestClientResponseException e) {
            logger.error("Experienced problems while getting response from {}, status code: {}",
                    WEATHER_API_URL,
                    e.getStatusCode().value()
            );
        } catch (ResourceAccessException e) {
            logger.error("Experienced problems while connecting to {}, status code: ", WEATHER_API_URL);
        } catch (JsonProcessingException e) {
            logger.error("Received XML is malformed or no longer follows previously defined specification");
        }
    }


}
