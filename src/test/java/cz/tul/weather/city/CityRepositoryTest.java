package cz.tul.weather.city;


import cz.tul.weather.country.Country;
import cz.tul.weather.country.CountryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class CityRepositoryTest {
    @Autowired
    private CityRepository underTest;
    @Autowired
    private CountryRepository countryRepository;

    @AfterEach
    void tearDown(){underTest.deleteAll();}

    @Test
    void itShouldFindCityByNameAndCountry(){
        //given
        String cityName = "Liberec";
        Country country = new Country("Czechia");
        countryRepository.save(country);
        City city = new City();
        city.setCountry(country);
        city.setName(cityName);
        city.setLatitude(50.7663);
        city.setLongitude(15.0543);
        underTest.save(city);
        //when
        Optional<City> exist = underTest.findCityByNameAndCountry(cityName,country);
        //then
        assertThat(exist).isPresent();
    }
}