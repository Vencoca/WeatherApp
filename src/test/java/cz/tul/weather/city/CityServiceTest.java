package cz.tul.weather.city;

import cz.tul.weather.country.Country;
import cz.tul.weather.country.CountryRepository;
import cz.tul.weather.exception.ApiRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock private CityRepository cityRepository;
    @Mock private CountryRepository countryRepository;
    private CityService underTest;

    @BeforeEach
    void setUp(){underTest = new CityService(cityRepository,countryRepository);}

    @Test
    void canGetAllCities() {
        //when
        underTest.getCities();
        //then
        verify(cityRepository).findAll();
    }

    @Test
    void canAddNewCity() {
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        City city = new City();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        //When
        underTest.addNewCity(countryName,city);
        //Then
        verify(cityRepository).save(city);
    }

    @Test
    void addNewCityShouldThrowExceptionWhenCountryDoesntExist(){
        //Given
        String countryName = "Czechia";
        City city = new City();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> underTest.addNewCity(countryName,city))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Country with name " + countryName + " does not exist!");
    }

    @Test
    void addNewCityShouldThrowExceptionWhenCityAlreadyExists(){
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        City city = new City();
        String cityName = "Liberec";
        city.setName(cityName);
        given(cityRepository.findCityByNameAndCountry(cityName,country)).willReturn(Optional.of(city));
        //When
        //Then
        assertThatThrownBy(() -> underTest.addNewCity(countryName,city))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("City with this name " + city.getName() + " already exists in this country!");
    }

    @Test
    void canGetCityInCountry() {
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        City city = new City();
        String cityName = "Liberec";
        given(cityRepository.findCityByNameAndCountry(cityName,country)).willReturn(Optional.of(city));
        //When
        underTest.getCityInCountry(countryName,cityName);
        //Then
        verify(cityRepository).findCityByNameAndCountry(cityName,country);
    }

    @Test
    void getCityInCountryShouldThrowExceptionWhenCountryDoesntExist() {
        //Given
        String countryName = "Czechia";
        String cityName = "Liberec";
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> underTest.getCityInCountry(countryName,cityName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Country with name " + countryName + " does not exist!");
    }

    @Test
    void getCityInCountryShouldThrowExceptionWhenCityDoesntExist() {
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        City city = new City();
        String cityName = "Liberec";
        city.setName(cityName);
        //When
        //Then
        assertThatThrownBy(() -> underTest.getCityInCountry(countryName,cityName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("City with name " + cityName + " does not exist!");
    }

    @Test
    void canDeleteCity() {
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        City city = new City();
        String cityName = "Liberec";
        given(cityRepository.findCityByNameAndCountry(cityName,country)).willReturn(Optional.of(city));
        //When
        underTest.deleteCity(countryName,cityName);
        //Then
        verify(cityRepository).deleteById(city.getId());
    }

    @Test
    void deleteCityShouldThrowExceptionWhenCountryDoesntExist() {
        //Given
        String countryName = "Czechia";
        String cityName = "Liberec";
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> underTest.deleteCity(countryName,cityName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Country with name " + countryName + " does not exist!");
    }

    @Test
    void deleteCityShouldThrowExceptionWhenCityDoesntExist() {
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        City city = new City();
        String cityName = "Liberec";
        city.setName(cityName);
        //When
        //Then
        assertThatThrownBy(() -> underTest.deleteCity(countryName,cityName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("City with name " + cityName + " does not exist!");
    }

    @Test
    void canUpdateCity() {
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        City city = new City();
        String cityName = "Liberec";
        city.setName(cityName);
        given(cityRepository.findCityByNameAndCountry(cityName,country)).willReturn(Optional.of(city));
        String nameNew = "Praha";
        Double longitudeNew = 50.0755;
        Double latitudeNew = 14.4378;
        String countryNameNew = "Poland";
        Country countryNew = new Country(countryNameNew);
        given(countryRepository.findCountryByName(countryNameNew)).willReturn(Optional.of(countryNew));
        //When
        underTest.updateCity(countryName,cityName,nameNew,longitudeNew,latitudeNew,countryNameNew);
        //Then
        assertThat(city.getName()).isEqualTo(nameNew);
        assertThat(city.getLatitude()).isEqualTo(latitudeNew);
        assertThat(city.getLongitude()).isEqualTo(longitudeNew);
        assertThat(city.getCountry()).isEqualTo(countryNew);
    }

    @Test
    void updateCityShouldThrowExceptionWhenCountryDoesntExist() {
        //Given
        String countryName = "Czechia";
        String cityName = "Liberec";
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.empty());
        String nameNew = "Praha";
        Double longitudeNew = 50.0755;
        Double latitudeNew = 14.4378;
        String countryNameNew = "Poland";
        //When
        //Then
        assertThatThrownBy(() -> underTest.updateCity(countryName,cityName,nameNew,longitudeNew,latitudeNew,countryNameNew))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Country with name " + countryName + " does not exist!");
    }

    @Test
    void updateCityShouldThrowExceptionWhenCityDoesntExist() {
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        City city = new City();
        String cityName = "Liberec";
        city.setName(cityName);
        String nameNew = "Praha";
        Double longitudeNew = 50.0755;
        Double latitudeNew = 14.4378;
        String countryNameNew = "Poland";
        //When
        //Then
        assertThatThrownBy(() -> underTest.updateCity(countryName,cityName,nameNew,longitudeNew,latitudeNew,countryNameNew))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("City with name " + cityName + " does not exist!");
    }

    @Test
    void updateCityShouldThrowExceptionWhenNewCountryDoesntExist(){
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        City city = new City();
        String cityName = "Liberec";
        city.setName(cityName);
        given(cityRepository.findCityByNameAndCountry(cityName,country)).willReturn(Optional.of(city));
        String nameNew = "Praha";
        Double longitudeNew = 50.0755;
        Double latitudeNew = 14.4378;
        String countryNameNew = "Poland";
        //When
        //Then
        assertThatThrownBy(() -> underTest.updateCity(countryName,cityName,nameNew,longitudeNew,latitudeNew,countryNameNew))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Country with name " + countryNameNew + " does not exist!");
    }
}