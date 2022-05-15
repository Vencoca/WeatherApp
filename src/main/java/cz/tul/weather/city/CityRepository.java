package cz.tul.weather.city;

import cz.tul.weather.country.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City,Long> {
    Optional<City> findCityByNameAndCountry(String name, Country country);
}
