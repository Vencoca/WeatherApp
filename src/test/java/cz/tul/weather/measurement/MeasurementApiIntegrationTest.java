package cz.tul.weather.measurement;

import cz.tul.weather.city.City;
import cz.tul.weather.city.CityRepository;
import cz.tul.weather.country.Country;
import cz.tul.weather.country.CountryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MeasurementApiIntegrationTest {
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private MockMvc mockMvc;

    private static final Country czechia = new Country("Czechia");
    private static final City liberec = new City("Liberec",15.0543,50.7663,czechia);
    private static final Measurement measurementOldest = new Measurement(2.0,12.4,1024L,30,5.2,120, Instant.now().minus(8, ChronoUnit.DAYS));
    private static final Measurement measurementOlder = new Measurement(4.0,12.4,1024L,30,5.2,120, Instant.now().minus(3, ChronoUnit.DAYS));
    private static final Measurement measurementNew = new Measurement(6.0,12.4,1024L,30,5.2,120, Instant.now());

    @BeforeAll
    public void beforeAll() {
        countryRepository.save(czechia);
        cityRepository.save(liberec);
    }

    @BeforeEach
    void setUp() {
        measurementRepository.save(measurementNew.getPoint("Czechia","Liberec"));
        measurementRepository.save(measurementOlder.getPoint("Czechia","Liberec"));
        measurementRepository.save(measurementOldest.getPoint("Czechia","Liberec"));
    }

    @AfterEach
    void tearDown() {
        measurementRepository.deleteAll("Liberec","Czechia");
    }

    @Test
    void getMeasurementsShouldWorkTroughAllLayers() throws Exception{
        mockMvc.perform(get("/api/v1/country/Czechia/Liberec/history"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{" +
                                "\"temp\": 2.0," +
                                "\"feelsLike\": 12.4," +
                                "\"pressure\": 1024," +
                                "\"humidity\": 30," +
                                "\"windSpeed\": 5.2," +
                                "\"windDeg\": 120" +
                                "},{" +
                                "\"temp\": 4.0," +
                                "\"feelsLike\": 12.4," +
                                "\"pressure\": 1024," +
                                "\"humidity\": 30," +
                                "\"windSpeed\": 5.2," +
                                "\"windDeg\": 120" +
                                "},{" +
                                "\"temp\": 6.0," +
                                "\"feelsLike\": 12.4," +
                                "\"pressure\": 1024," +
                                "\"humidity\": 30," +
                                "\"windSpeed\": 5.2," +
                                "\"windDeg\": 120" +
                                "}]"
                ));
    }

    @Test
    void getMeasurementsShouldThrowExceptionWhenCountryDoesntExist() throws Exception{
        mockMvc.perform(get("/api/v1/country/Czechiaa/Liberec/history"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Czechiaa does not exist!")));
    }

    @Test
    void getMeasurementsShouldThrowExceptionWhenCityDoesntExist() throws Exception{
        mockMvc.perform(get("/api/v1/country/Czechia/Liberecc/history"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("City with name Liberecc does not exist!")));
    }

    @Test
    void getMeasurementShouldWorkTroughAllLayers() throws Exception{
        mockMvc.perform(get("/api/v1/country/Czechia/Liberec/weather"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{" +
                                "\"temp\": 6.0," +
                                "\"feelsLike\": 12.4," +
                                "\"pressure\": 1024," +
                                "\"humidity\": 30," +
                                "\"windSpeed\": 5.2," +
                                "\"windDeg\": 120" +
                                "}"
                ));

    }

    @Test
    void getMeasurementShouldReturnNullWhenNoMeasurements() throws Exception{
        Country country = new Country("Poland");
        City city = new City("Liberec",15.0543,50.7663,country);
        countryRepository.save(country);
        cityRepository.save(city);

        mockMvc.perform(get("/api/v1/country/Poland/Liberec/weather"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        cityRepository.delete(city);
        countryRepository.delete(country);
    }

    @Test
    void getMeasurementShouldThrowExceptionWhenCountryDoesntExist() throws Exception{
        mockMvc.perform(get("/api/v1/country/Czechiaa/Liberec/weather"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Czechiaa does not exist!")));
    }

    @Test
    void getMeasurementShouldThrowExceptionWhenCityDoesntExist() throws Exception{
        mockMvc.perform(get("/api/v1/country/Czechia/Liberecc/weather"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("City with name Liberecc does not exist!")));
    }


    @Test
    void getAverageShouldWorkTroughAllLayers() throws Exception{
        mockMvc.perform(get("/api/v1/country/Czechia/Liberec/avg"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{" +
                                "\"temp\": 4.0," +
                                "\"feelsLike\": 12.4," +
                                "\"pressure\": 1024," +
                                "\"humidity\": 30," +
                                "\"windSpeed\": 5.2," +
                                "\"windDeg\": 120" +
                                "},{" +
                                "\"temp\": 5.0," +
                                "\"feelsLike\": 12.4," +
                                "\"pressure\": 1024," +
                                "\"humidity\": 30," +
                                "\"windSpeed\": 5.2," +
                                "\"windDeg\": 120" +
                                "},{" +
                                "\"temp\": 6.0," +
                                "\"feelsLike\": 12.4," +
                                "\"pressure\": 1024," +
                                "\"humidity\": 30," +
                                "\"windSpeed\": 5.2," +
                                "\"windDeg\": 120" +
                                "}]"
                ));

    }

    @Test
    void getAverageShouldThrowExceptionWhenCountryDoesntExist() throws Exception{
        mockMvc.perform(get("/api/v1/country/Czechiaa/Liberec/avg"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Czechiaa does not exist!")));
    }

    @Test
    void getAverageShouldThrowExceptionWhenCityDoesntExist() throws Exception{
        mockMvc.perform(get("/api/v1/country/Czechia/Liberecc/avg"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("City with name Liberecc does not exist!")));
    }

    @Test
    void getCsvShouldWorkTroughAllLayers() throws Exception{
        mockMvc.perform(get("/api/v1/country/Czechia/Liberec/history/csv"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("2.0,12.4,1024,30,5.2,120"),
                        containsString("4.0,12.4,1024,30,5.2,120"),
                        containsString("6.0,12.4,1024,30,5.2,120")
                        )
                ));

    }

    @Test
    void getCsvShouldThrowExceptionWhenCountryDoesntExist() throws Exception{
        mockMvc.perform(get("/api/v1/country/Czechiaa/Liberec/history/csv"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Czechiaa does not exist!")));
    }

    @Test
    void getCsvShouldThrowExceptionWhenCityDoesntExist() throws Exception{
        mockMvc.perform(get("/api/v1/country/Czechia/Liberecc/history/csv"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("City with name Liberecc does not exist!")));
    }

    @Test
    void uploadCsvShouldWorkTroughAllLayers() throws Exception{
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "measurement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                ("15.43,14.86,1021,70,2.75,132," + Instant.now()).getBytes()
        );
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/country/Czechia/Liberec/history/csv").file(file))
                .andExpect(status().isOk());

        Measurement saved = measurementRepository.findMeasurementByCountryAndCity("Liberec","Czechia");
        assertThat(saved.getTemp()).isEqualTo(15.43);
        assertThat(saved.getFeelsLike()).isEqualTo(14.86);
        assertThat(saved.getPressure()).isEqualTo(1021);
        assertThat(saved.getHumidity()).isEqualTo(70);
        assertThat(saved.getWindSpeed()).isEqualTo(2.75);
        assertThat(saved.getWindDeg()).isEqualTo(132);
    }

    @Test
    void uploadCsvShouldThrowExceptionWhenCsvHasTooManyArgumentsInLine() throws Exception{
        String line = ("15.43,14.86,1021,70,2.75,132,IMWRONG," + Instant.now());
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "measurement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                (line.getBytes())
        );
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/country/Czechia/Liberec/history/csv").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Too many arguments in line: " + line)));

    }

    @Test
    void uploadCsvShouldThrowExceptionWhenCsvHasWrongNumber() throws Exception{
        String line = ("15.43,14.86,1021.5,70,2.75,132," + Instant.now());
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "measurement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                (line.getBytes())
        );
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/country/Czechia/Liberec/history/csv").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error cant parse file!")));

    }

    @Test
    void uploadCsvShouldThrowExceptionWhenCountryDoesntExist() throws Exception{
        String line = ("15.43,14.86,1021.5,70,2.75,132," + Instant.now());
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "measurement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                (line.getBytes())
        );
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/country/Czechiaa/Liberec/history/csv").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Czechiaa does not exist!")));
    }

    @Test
    void uploadCsvShouldThrowExceptionWhenCityDoesntExist() throws Exception{
        String line = ("15.43,14.86,1021.5,70,2.75,132," + Instant.now());
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "measurement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                (line.getBytes())
        );
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/country/Czechia/Liberecc/history/csv").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("City with name Liberecc does not exist!")));
    }


    @Test
    void deleteAllMeasurementsShouldWorkTroughAllLayers() throws Exception{
        mockMvc.perform(delete("/api/v1/country/Czechia/Liberec/history/"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAllMeasurementsShouldThrowExceptionWhenCountryDoesntExist() throws Exception{
        mockMvc.perform(delete("/api/v1/country/Czechiaa/Liberec/history/"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Czechiaa does not exist!")));
    }

    @Test
    void deleteAllMeasurementsShouldThrowExceptionWhenCityDoesntExist() throws Exception{
        mockMvc.perform(delete("/api/v1/country/Czechia/Liberecc/history/"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("City with name Liberecc does not exist!")));
    }
}