package cz.tul.weather.measurement;

import com.influxdb.client.write.Point;
import cz.tul.weather.city.City;
import cz.tul.weather.city.CityRepository;
import cz.tul.weather.country.Country;
import cz.tul.weather.country.CountryRepository;
import cz.tul.weather.exception.ApiRequestException;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {
    @Mock private CityRepository cityRepository;
    @Mock private CountryRepository countryRepository;
    @Mock private MeasurementRepository measurementRepository;
    private MeasurementService underTest;

    @BeforeEach
    void setUp(){underTest = new MeasurementService(measurementRepository,countryRepository,cityRepository);}

    @Test
    void canGetAllMeasurementForCity() {
        //Given
        String cityName = "Liberec";
        City city = new City();
        String countryName = "Czechia";
        Country country = new Country();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        given(cityRepository.findCityByNameAndCountry(cityName,country)).willReturn(Optional.of(city));
        //When
        underTest.getAllMeasurementForCity(cityName,countryName);
        //Then
        verify(measurementRepository).findMeasurementsByCountryAndCity(cityName,countryName);
    }

    @Test
    void getAllMeasurementForCityShouldThrowExceptionWhenCountryDoesntExist(){
        //Given
        String cityName = "Liberec";
        String countryName = "Czechia";
        //When
        //Then
        assertThatThrownBy(() -> underTest.getAllMeasurementForCity(cityName,countryName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Country with name " + countryName + " does not exist!");
    }

    @Test
    void getAllMeasurementForCityShouldThrowExceptionWhenCityDoesntExist(){
        //Given
        String cityName = "Liberec";
        String countryName = "Czechia";
        Country country = new Country();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        //When
        //Then
        assertThatThrownBy(() -> underTest.getAllMeasurementForCity(cityName,countryName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("City with name " + cityName + " does not exist!");
    }

    @Test
    void canGetAverageForCity() {
        //Given
        String cityName = "Liberec";
        City city = new City();
        String countryName = "Czechia";
        Country country = new Country();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        given(cityRepository.findCityByNameAndCountry(cityName,country)).willReturn(Optional.of(city));
        //When
        underTest.getAverageForCity(cityName,countryName);
        //Then
        verify(measurementRepository).findAveragesByCountryAndCity(cityName,countryName);
    }
    @Test
    void getAverageForCityShouldThrowExceptionWhenCountryDoesntExist() {
        //Given
        String cityName = "Liberec";
        String countryName = "Czechia";
        //When
        //Then
        assertThatThrownBy(() -> underTest.getAverageForCity(cityName,countryName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Country with name " + countryName + " does not exist!");
    }
    @Test
    void getAverageForCityShouldThrowExceptionWhenCityDoesntExist() {
        //Given
        String cityName = "Liberec";
        String countryName = "Czechia";
        Country country = new Country();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        //When
        //Then
        assertThatThrownBy(() -> underTest.getAverageForCity(cityName,countryName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("City with name " + cityName + " does not exist!");
    }

    @Test
    void canGetLastMeasurementForCity() {
        //Given
        String cityName = "Liberec";
        City city = new City();
        String countryName = "Czechia";
        Country country = new Country();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        given(cityRepository.findCityByNameAndCountry(cityName,country)).willReturn(Optional.of(city));
        //When
        underTest.getLastMeasurementForCity(cityName,countryName);
        //Then
        verify(measurementRepository).findMeasurementByCountryAndCity(cityName,countryName);
    }
    @Test
    void getLastMeasurementForCityShouldThrowExceptionWhenCountryDoesntExist() {
        //Given
        String cityName = "Liberec";
        String countryName = "Czechia";
        //When
        //Then
        assertThatThrownBy(() -> underTest.getLastMeasurementForCity(cityName,countryName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Country with name " + countryName + " does not exist!");
    }
    @Test
    void getLastMeasurementForCityShouldThrowExceptionWhenCityDoesntExist() {
        //Given
        String cityName = "Liberec";
        String countryName = "Czechia";
        Country country = new Country();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        //When
        //Then
        assertThatThrownBy(() -> underTest.getLastMeasurementForCity(cityName,countryName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("City with name " + cityName + " does not exist!");
    }

    @Test
    void canGetMeasurementsForCityInCsv() throws JSONException {
        //Given
        String cityName = "Liberec";
        City city = new City();
        String countryName = "Czechia";
        Country country = new Country();
        String JSONString = "{\"coord\":{\"lon\":15.0543,\"lat\":50.7663},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"base\":\"stations\",\"main\":{\"temp\":14.39,\"feels_like\":14.13,\"temp_min\":13.35,\"temp_max\":15.06,\"pressure\":1012,\"humidity\":86,\"sea_level\":1012,\"grnd_level\":970},\"visibility\":10000,\"wind\":{\"speed\":0.89,\"deg\":70},\"clouds\":{\"all\":100},\"dt\":1653430798,\"sys\":{\"type\":2,\"id\":47775,\"country\":\"CZ\",\"sunrise\":1653447523,\"sunset\":1653504901},\"timezone\":7200,\"id\":3071961,\"name\":\"Liberec\",\"cod\":200}";
        Measurement measurement = new Measurement(JSONString);
        List<Measurement> list = new ArrayList<>();
        list.add(measurement);
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        given(cityRepository.findCityByNameAndCountry(cityName,country)).willReturn(Optional.of(city));
        given(measurementRepository.findMeasurementsByCountryAndCity(cityName,countryName)).willReturn(list);
        //When
        ByteArrayInputStream byteArray = underTest.getMeasurementsForCityInCsv(cityName,countryName);
        String returnedValue = new String(byteArray.readAllBytes(), StandardCharsets.UTF_8);
        //Then
        verify(measurementRepository).findMeasurementsByCountryAndCity(cityName,countryName);
        ByteArrayInputStream expectedBA = new ByteArrayInputStream((measurement.toCsv() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
        String expected = new String(expectedBA.readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expected,returnedValue);
    }
    @Test
    void getMeasurementsForCityInCsvShouldThrowExceptionWhenCountryDoesntExist() {
        //Given
        String cityName = "Liberec";
        String countryName = "Czechia";
        //When
        //Then
        assertThatThrownBy(() -> underTest.getMeasurementsForCityInCsv(cityName,countryName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Country with name " + countryName + " does not exist!");
    }
    @Test
    void getMeasurementsForCityInCsvShouldThrowExceptionWhenCityDoesntExist() {
        //Given
        String cityName = "Liberec";
        String countryName = "Czechia";
        Country country = new Country();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        //When
        //Then
        assertThatThrownBy(() -> underTest.getMeasurementsForCityInCsv(cityName,countryName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("City with name " + cityName + " does not exist!");
    }

    @Test
    void CanAddNewMeasurements() {
        //Given
        String cityName = "Liberec";
        City city = new City();
        String countryName = "Czechia";
        Country country = new Country();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        given(cityRepository.findCityByNameAndCountry(cityName,country)).willReturn(Optional.of(city));
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "measurement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "15.43,14.86,1021,70,2.75,132,2022-05-20T00:00:01Z".getBytes()
        );
        Measurement measurement = new Measurement();
        measurement.setTemp(15.43);
        measurement.setFeelsLike(14.86);
        measurement.setPressure(1021L);
        measurement.setHumidity(70);
        measurement.setWindSpeed(2.75);
        measurement.setWindDeg(132);
        measurement.setTime(Instant.parse("2022-05-20T00:00:01Z"));
        Point point = measurement.getPoint(countryName,cityName);
        //When
        underTest.addNewMeasurements(cityName,countryName,file);
        //Then
        ArgumentCaptor<Point> pointArgumentCaptor = ArgumentCaptor.forClass(Point.class);
        verify(measurementRepository).save(pointArgumentCaptor.capture());
        Point capturedPoint = pointArgumentCaptor.getValue();
        assertThat(capturedPoint.toLineProtocol()).isEqualTo(point.toLineProtocol());
    }


    @Test
    void addNewMeasurementsShouldThrowExceptionWhenCountryDoesntExist() {
        //Given
        String cityName = "Liberec";
        String countryName = "Czechia";
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "measurement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "15.43,14.86,1021,70,2.75,132,2022-05-20T00:00:01Z".getBytes()
        );
        //When
        //Then
        assertThatThrownBy(() -> underTest.addNewMeasurements(cityName,countryName,file))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Country with name " + countryName + " does not exist!");
    }
    @Test
    void addNewMeasurementsShouldThrowExceptionWhenCityDoesntExist() {
        //Given
        String cityName = "Liberec";
        String countryName = "Czechia";
        Country country = new Country();
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "measurement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "15.43,14.86,1021,70,2.75,132,2022-05-20T00:00:01Z".getBytes()
        );
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        //When
        //Then
        assertThatThrownBy(() -> underTest.addNewMeasurements(cityName,countryName,file))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("City with name " + cityName + " does not exist!");
    }
    @Test
    void addNewMeasurementsShouldThrowExceptionWhenCsvHaveLineWithTooManyArguments() {
        //Given
        String cityName = "Liberec";
        City city = new City();
        String countryName = "Czechia";
        Country country = new Country();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        given(cityRepository.findCityByNameAndCountry(cityName,country)).willReturn(Optional.of(city));
        String line = "15.43,14.86,1021,70,2.75,132,2022-05-20T00:00:01Z,IMWRONG";
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "measurement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                line.getBytes()
        );
        //When
        //Then
        assertThatThrownBy(() -> underTest.addNewMeasurements(cityName,countryName,file))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Too many arguments in line: " + line);
    }
    @Test
    void addNewMeasurementsShouldThrowExceptionWhenNumberFormatIsWrong() {
        //Given
        String cityName = "Liberec";
        City city = new City();
        String countryName = "Czechia";
        Country country = new Country();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        given(cityRepository.findCityByNameAndCountry(cityName,country)).willReturn(Optional.of(city));
        String line = "15.43,14.86,1021.2,70,2.75,132,2022-05-20T00:00:01Z";
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "measurement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                line.getBytes()
        );
        //When
        //Then
        assertThatThrownBy(() -> underTest.addNewMeasurements(cityName,countryName,file))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Error cant parse file!");
    }

    @Test
    void canDeleteAllMeasurementsForCity() {
        //Given
        String cityName = "Liberec";
        City city = new City();
        String countryName = "Czechia";
        Country country = new Country();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        given(cityRepository.findCityByNameAndCountry(cityName,country)).willReturn(Optional.of(city));
        //When
        underTest.deleteAllMeasurementsForCity(cityName,countryName);
        //Then
        verify(measurementRepository).deleteAll(cityName,countryName);
    }

    @Test
    void deleteAllMeasurementsForCityShouldThrowExceptionWhenCountryDoesntExist() {
        //Given
        String cityName = "Liberec";
        String countryName = "Czechia";
        //When
        //Then
        assertThatThrownBy(() -> underTest.deleteAllMeasurementsForCity(cityName,countryName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("Country with name " + countryName + " does not exist!");
    }
    @Test
    void deleteAllMeasurementsForCityShouldThrowExceptionWhenCityDoesntExist() {
        //Given
        String cityName = "Liberec";
        String countryName = "Czechia";
        Country country = new Country();
        given(countryRepository.findCountryByName(countryName)).willReturn(Optional.of(country));
        //When
        //Then
        assertThatThrownBy(() -> underTest.deleteAllMeasurementsForCity(cityName,countryName))
                .isInstanceOf(ApiRequestException.class)
                .hasMessage("City with name " + cityName + " does not exist!");
    }
}