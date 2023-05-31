package dev.artur.deliveryfeeservice.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

import static java.util.stream.Collectors.joining;

@JacksonXmlRootElement(localName = "observations")
public class ObservationsDTO {
    @JacksonXmlProperty(localName = "timestamp")
    private Long timestamp;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "station")
    private List<WeatherStationDTO> stations;

    public List<WeatherStationDTO> getStations() {
        return stations;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ObservationsDTO{\n" +
                "  timestamp=" + timestamp + ",\n" +
                "  stations=" + stations.stream().map(WeatherStationDTO::toString).collect(joining("\n    ", "\n    ", "\n")) +
                '}';
    }

}
