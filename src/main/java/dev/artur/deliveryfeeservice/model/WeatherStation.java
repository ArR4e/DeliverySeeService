package dev.artur.deliveryfeeservice.model;

import jakarta.persistence.*;

@Entity
public class WeatherStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private City city;

    private String WMOCode;

    private double airTemperature;

    private double windSpeed;

    private String weatherPhenomenon;

    private Long timestamp;

    public WeatherStation() {
    }

    public WeatherStation(City city, String WMOCode, double airTemperature, double windSpeed, String weatherPhenomenon) {
        this.city = city;
        this.WMOCode = WMOCode;
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
        this.weatherPhenomenon = weatherPhenomenon;
    }

    public WeatherStation(City city, String WMOCode, double airTemperature, double windSpeed, String weatherPhenomenon, Long timestamp) {
        this.city = city;
        this.WMOCode = WMOCode;
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
        this.weatherPhenomenon = weatherPhenomenon;
        this.timestamp = timestamp;
    }


    public Long getId() {
        return id;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public City getCity() {
        return city;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public String getWeatherPhenomenon() {
        return weatherPhenomenon;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "WeatherStation{" +
                "id=" + id +
                ", city='" + city.name() + '\'' +
                ", WMOCode='" + WMOCode + '\'' +
                ", airTemperature=" + airTemperature +
                ", windSpeed=" + windSpeed +
                ", weatherPhenomenon='" + weatherPhenomenon + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
