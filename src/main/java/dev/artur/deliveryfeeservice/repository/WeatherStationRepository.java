package dev.artur.deliveryfeeservice.repository;

import dev.artur.deliveryfeeservice.model.City;
import dev.artur.deliveryfeeservice.model.WeatherStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WeatherStationRepository extends JpaRepository<WeatherStation, Long> {
    @Query("select ws from WeatherStation ws where ws.city=?1 order by ws.timestamp desc limit 1")
    Optional<WeatherStation> findRecentByCity(City city);
//    Optional<WeatherStation> findFirstByCityOrderByTimestamp(City city);
}
