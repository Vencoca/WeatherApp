package cz.tul.weather.city;

import cz.tul.weather.country.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRespository extends JpaRepository<City,Long> {
    Optional<City> findCityByName(String name);
    Optional<City> findCityByNameAndCountry(String name, Country country);
}
