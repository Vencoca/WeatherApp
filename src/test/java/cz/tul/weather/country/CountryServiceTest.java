package cz.tul.weather.country;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock private CountryRepository countryRepository;
    private CountryService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CountryService(countryRepository);
    }

    @Test
    void canGetAllCountries() {
        //when
        underTest.getAllCountries();
        //then
        verify(countryRepository).findAll();
    }

    @Test
    void getCountry() {
    }

    @Test
    void canAddNewCountry() {
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        //when
        underTest.addNewCountry(country);
        //then
        ArgumentCaptor<Country> countryArgumentCaptor = ArgumentCaptor.forClass(Country.class);
        verify(countryRepository).save(countryArgumentCaptor.capture());

        Country capturedCountry = countryArgumentCaptor.getValue();
        assertThat(capturedCountry).isEqualTo(country);
    }

    @Test
    void deleteCountry() {
    }

    @Test
    void updateCountry() {
    }
}