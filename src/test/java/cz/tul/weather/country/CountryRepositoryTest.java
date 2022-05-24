package cz.tul.weather.country;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class CountryRepositoryTest {

    @Autowired
    private CountryRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldFindCountryByName() {
        //given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        underTest.save(country);
        //when
        Optional<Country> exists = underTest.findCountryByName(countryName);
        //then
        assertThat(exists).isPresent();
    }

    @Test
    void itShouldNotFindCountryByName(){
        //given
        String countryName = "Czechia";
        //when
        Optional<Country> exists = underTest.findCountryByName(countryName);
        //then
        assertThat(exists).isNotPresent();
    }

    @Test
    @Transactional
    void itShouldDeleteCountryByName() {
        //given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        underTest.save(country);
        //when
        underTest.deleteByName("Czechia");
        //then
        Optional<Country> result = underTest.findCountryByName(countryName);
        assertThat(result).isNotPresent();
    }
}
