package cz.tul.weather.country;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
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
    void canGetCountry(){
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        given(countryRepository.findCountryByName(anyString())).willReturn(Optional.of(country));
        //when
        underTest.getCountry(countryName);
        //then
        ArgumentCaptor<String> nameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(countryRepository).findCountryByName(nameArgumentCaptor.capture());
        String capturedName = nameArgumentCaptor.getValue();
        assertThat(capturedName).isEqualTo(countryName);
    }

    @Test
    void getCountryShouldThrowExceptionWhenCountryNameWhenNameDoesNotExist() {
        //Given
        String countryName = "Czechia";
        //When
        //Then
        assertThatThrownBy(() -> underTest.getCountry(countryName))
                .hasMessage("Country with name " + countryName + " does not exists!");
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
    void addNewCountryShouldThrowExceptionWhenCountryNameTaken(){
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        given(countryRepository.findCountryByName(anyString())).willReturn(Optional.of(country));
        //when
        //then
        assertThatThrownBy(() -> underTest.addNewCountry(country))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Name taken");
    }

    @Test
    void addNewCountryShouldThrowExceptionWhenCountryNameEmpty(){
        //Given
        String countryName = "";
        Country country = new Country(countryName);
        //When
        //Then
        assertThatThrownBy(() -> underTest.addNewCountry(country))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Name cant be empty");
    }

    @Test
    void canDeleteCountry() {
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        given(countryRepository.findCountryByName(anyString())).willReturn(Optional.of(country));
        //when
        underTest.deleteCountry(countryName);
        //then
        ArgumentCaptor<String> nameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(countryRepository).deleteByName(nameArgumentCaptor.capture());
        String capturedName = nameArgumentCaptor.getValue();
        assertThat(capturedName).isEqualTo(countryName);
    }

    @Test
    void deleteCountryShouldThrowExceptionWhenCountryDoesntExist(){
        //Given
        String countryName = "Czechia";
        //when
        //then
        assertThatThrownBy(() -> underTest.deleteCountry(countryName))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Country with name " + countryName + " does not exist!");
    }

    @Test
    void canUpdateCountry(){
        //Given
        String countryName = "Czechia";
        String countryNameNew = "Poland";
        Country country = new Country(countryName);
        Country countryNew = new Country(countryNameNew);
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        given(countryRepository.findCountryByName(countryNameNew)).willReturn(Optional.empty());
        //when
        underTest.updateCountry(countryName,countryNew);
        //then
        assertThat(country.getName()).isEqualTo(countryNameNew);
    }

    @Test
    void updateCountryShouldThrowExceptionWhenNewCountryExist() {
        //Given
        String countryName = "Czechia";
        String countryNameNew = "Poland";
        Country country = new Country(countryName);
        Country countryNew = new Country(countryNameNew);
        given(countryRepository.findCountryByName(anyString())).willReturn(Optional.of(country));
        //when
        //then
        assertThatThrownBy(() -> underTest.updateCountry(countryName,countryNew))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Country with name " + countryNameNew + " already exist!");
    }

    @Test
    void updateCountryShouldThrowExceptionWhenCountryDoesntExist(){
        //Given
        String countryName = "Czechia";
        String countryNameNew = "Poland";
        Country countryNew = new Country(countryNameNew);
        //when
        //then
        assertThatThrownBy(() -> underTest.updateCountry(countryName,countryNew))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Country with name " + countryName + " does not exist!");
    }

    @Test
    void canDeleteCountries(){
        //when
        underTest.deleteCountries();
        //then
        verify(countryRepository).deleteAll();
    }

    @Test
    void canAddNewCountries() {
        //Given
        String name1 = "Poland";
        Country country1 = new Country(name1);
        String name2 = "Czechia";
        Country country2 = new Country(name2);
        List<Country> listOfCountries = new ArrayList<>();
        listOfCountries.add(country1);
        listOfCountries.add(country2);
        //when
        underTest.addNewCountries(listOfCountries);
        //then
        ArgumentCaptor<Country> countryArgumentCaptor = ArgumentCaptor.forClass(Country.class);
        verify(countryRepository,times(2)).save(countryArgumentCaptor.capture());

        List<Country> capturedCountries = countryArgumentCaptor.getAllValues();
        assertThat(capturedCountries).isEqualTo(listOfCountries);
    }

    @Test
    void addNewCountriesShouldThrowExceptionWhenCountryNameTaken(){
        //Given
        String notTakenName = "Poland";
        Country country1 = new Country(notTakenName);
        String takenName = "Czechia";
        Country country2 = new Country(takenName);
        List<Country> listOfCountries = new ArrayList<>();
        listOfCountries.add(country1);
        listOfCountries.add(country2);
        given(countryRepository.findCountryByName(notTakenName)).willReturn(Optional.empty());
        given(countryRepository.findCountryByName(takenName)).willReturn(Optional.of(country1));
        //when
        //then
        assertThatThrownBy(() -> underTest.addNewCountries(listOfCountries))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Name " + takenName + " already taken");
    }

    @Test
    void addNewCountriesShouldThrowExceptionWhenCountryNameEmpty(){
        //Given
        Country country1 = new Country("");
        Country country2 = new Country("Czechia");
        List<Country> listOfCountries = new ArrayList<>();
        listOfCountries.add(country1);
        listOfCountries.add(country2);
        //When
        //Then
        assertThatThrownBy(() -> underTest.addNewCountries(listOfCountries))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Name cant be empty");
    }

}