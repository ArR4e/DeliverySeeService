package dev.artur.deliveryfeeservice.service;

import dev.artur.deliveryfeeservice.exception.ForbiddenVehicleUsageException;
import dev.artur.deliveryfeeservice.exception.NoDataAvailableException;
import dev.artur.deliveryfeeservice.model.City;
import dev.artur.deliveryfeeservice.model.Vehicle;
import dev.artur.deliveryfeeservice.model.WeatherStation;
import dev.artur.deliveryfeeservice.repository.WeatherStationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static dev.artur.deliveryfeeservice.model.Vehicle.BIKE;
import static dev.artur.deliveryfeeservice.model.Vehicle.SCOOTER;

@Service
public class DeliveryFeeService {

    //Create classifications of weather phenomenon
    private final static Set<String> SNOW_SLEET = new HashSet<>();
    private final static Set<String> RAIN = new HashSet<>();
    //Weather phenomen, when usage of vehicle is forbidden
    private final static Set<String> EXTREME_WEATHER = new HashSet<>();
    private static final BigDecimal ZERO = new BigDecimal("0.0");
    private static final BigDecimal ONE = new BigDecimal("1.0");


    static {
        SNOW_SLEET.add("Light snow shower");
        SNOW_SLEET.add("Moderate snow shower");
        SNOW_SLEET.add("Heavy snow shower");
        SNOW_SLEET.add("Light sleet");
        SNOW_SLEET.add("Moderate sleet");
        SNOW_SLEET.add("Light snowfall");
        SNOW_SLEET.add("Moderate snowfall");
        SNOW_SLEET.add("Heavy snowfall");
        SNOW_SLEET.add("Snowstorm");
        SNOW_SLEET.add("Drifting snow");
    }

    static {
        RAIN.add("Light shower");
        RAIN.add("Moderate shower");
        RAIN.add("Heavy shower");
        RAIN.add("Light rain");
        RAIN.add("Moderate rain");
        RAIN.add("Heavy rain");
    }

    static {
        EXTREME_WEATHER.add("Glaze");
        EXTREME_WEATHER.add("Hail");
        EXTREME_WEATHER.add("Thunder");
    }

    private final WeatherStationRepository weatherStationRepository;


    public DeliveryFeeService(WeatherStationRepository weatherStationRepository) {
        this.weatherStationRepository = weatherStationRepository;
    }

    //Regional-Base fee
    public static BigDecimal calculateRBF(City city, Vehicle vehicle) {
         return switch (city) {
            case TALLINN -> switch (vehicle) {
                case CAR -> new BigDecimal("4.0");
                case SCOOTER -> new BigDecimal("3.5");
                case BIKE -> new BigDecimal("3.0");
            };
            case TARTU -> switch (vehicle) {
                case CAR -> new BigDecimal("3.5");
                case SCOOTER -> new BigDecimal("3.0");
                case BIKE -> new BigDecimal("2.5");
            };
            case PARNU -> switch (vehicle) {
                case CAR -> new BigDecimal("3.0");
                case SCOOTER -> new BigDecimal("2.5");
                case BIKE -> new BigDecimal("2.0");
            };
        };
    }

    //Extra fee based on air temperature (ATEF) in a specific city
    public static BigDecimal calculateATEF(double airTemperature, Vehicle vehicle) {
        if (vehicle != SCOOTER && vehicle != BIKE) {
            return ZERO;
        }
        if (airTemperature < -10) {
            return ONE;
        }
        if (airTemperature < 0) {
            return new BigDecimal("0.5");
        }
        return ZERO;
    }

    //Extra fee based on wind speed (WSEF) in a specific city
    public static BigDecimal calculateWSEF(double windSpeed, Vehicle vehicle) {
        if (vehicle != BIKE) {
            return ZERO;
        }
        if (10 <= windSpeed && windSpeed <= 20) {
            return new BigDecimal("0.5");
        }
        if (windSpeed > 20) {
            throw new ForbiddenVehicleUsageException("Wind speed is higher than 20 m/s");
        }
        return ZERO;
    }

    //Extra fee based on weather phenomenon (WPEF) in a specific city
    public static BigDecimal calculateWPEF(String phenomenon, Vehicle vehicle) {
        if (vehicle != SCOOTER && vehicle != BIKE) {
            return ZERO;
        }
        if (EXTREME_WEATHER.contains(phenomenon)) {
            throw new ForbiddenVehicleUsageException(
                    "Weather phenomenon %s is too extreme for %s".formatted(
                            phenomenon.toLowerCase(),
                            vehicle.name().toLowerCase()
                    )
            );
        }
        //Weather phenomenon is related to rain
        if (RAIN.contains(phenomenon)) {
            return new BigDecimal("0.5");
        }
        //Weather phenomenon is related to sleet or snow
        if (SNOW_SLEET.contains(phenomenon)) {
            return ONE;
        }
        return ZERO;
    }

    public BigDecimal calculateDeliveryFee(City city, Vehicle vehicle) {
        BigDecimal baseFee = calculateRBF(city, vehicle);
        if (vehicle == Vehicle.CAR) {
            return baseFee;
        }
        WeatherStation weatherStation = weatherStationRepository.findRecentByCity(city)
                .orElseThrow(NoDataAvailableException::new);
        //Fee = RBF + ATEF + WPEF + WSEF
        return baseFee.add(calculateATEF(weatherStation.getAirTemperature(), vehicle))
                .add(calculateWPEF(weatherStation.getWeatherPhenomenon(), vehicle))
                .add(calculateWSEF(weatherStation.getWindSpeed(), vehicle));
    }

}
