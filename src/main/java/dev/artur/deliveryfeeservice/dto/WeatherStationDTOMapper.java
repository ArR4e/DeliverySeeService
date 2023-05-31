package dev.artur.deliveryfeeservice.dto;

import dev.artur.deliveryfeeservice.model.City;
import dev.artur.deliveryfeeservice.model.WeatherStation;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class WeatherStationDTOMapper implements Function<WeatherStationDTO, WeatherStation> {

    @Override
    public WeatherStation apply(WeatherStationDTO weatherStationDTO) {
        return new WeatherStation(
                City.nameToEnum(weatherStationDTO.getName()),
                weatherStationDTO.getWMOCode(),
                weatherStationDTO.getTemperature(),
                weatherStationDTO.getWindSpeed(),
                weatherStationDTO.getWeatherPhenomenon()
        );
    }


}
