package dev.artur.deliveryfeeservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherStationDTO {
    @JacksonXmlProperty(localName = "name")
    private String name;

    @JacksonXmlProperty(localName = "wmocode")
    private String WMOCode;

    @JacksonXmlProperty(localName = "airtemperature")
    private Double temperature;

    @JacksonXmlProperty(localName = "windspeed")
    private Double windSpeed;

    @JacksonXmlProperty(localName = "phenomenon")
    private String weatherPhenomenon;

    public String getName() {
        return name;
    }

    public String getWMOCode() {
        return WMOCode;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public String getWeatherPhenomenon() {
        return weatherPhenomenon;
    }

    @Override
    public String toString() {
        return "WeatherStationDTO{" +
                "name='" + name + '\'' +
                ", WMOCode='" + WMOCode + '\'' +
                ", temperature=" + temperature +
                ", windSpeed=" + windSpeed +
                ", weatherPhenomenon='" + weatherPhenomenon + '\'' +
                '}';
    }
}
