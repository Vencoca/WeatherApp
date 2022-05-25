package cz.tul.weather.city;

import cz.tul.weather.country.Country;
import cz.tul.weather.country.CountryRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CityApiIntegrationTest {
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private MockMvc mockMvc;

    private static final Country czechia = new Country("Czechia");
    private static final Country poland = new Country("Poland");
    private static final City liberec = new City("Liberec",15.0543,50.7663,czechia);
    private static final City warsaw = new City("Warsaw", 21.0122,52.2297,poland);

    @BeforeAll
    public void beforeAll() {
        countryRepository.save(czechia);
        countryRepository.save(poland);
    }

    @BeforeEach
    void setUp() {
        cityRepository.save(liberec);
        cityRepository.save(warsaw);
    }

    @AfterEach
    void tearDown() {
        cityRepository.deleteAll();
    }

    @Test
    void registerNewCityShouldWorkTroughAllLayers() throws Exception{
        mockMvc.perform(post("/api/v1/country/Czechia/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\" : \"Praha\"," +
                                "\"longitude\" : 50.0755," +
                                "\"latitude\" : 14.4378" +
                                "}")
                )
                .andExpect(status().isOk());
    }

    @Test
    void registerNewCityShouldWorkThrowExceptionWhenCountryDoesntExist() throws Exception{
        mockMvc.perform(post("/api/v1/country/Czechiaa/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\" : \"Praha\"," +
                                "\"longitude\" : 50.0755," +
                                "\"latitude\" : 14.4378" +
                                "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Czechiaa does not exist!")));
    }

    @Test
    void registerNewCityShouldWorkThrowExceptionWhenCityAlreadyExist() throws Exception{
        mockMvc.perform(post("/api/v1/country/Czechia/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\" : \"Liberec\"," +
                                "\"longitude\" : 50.0755," +
                                "\"latitude\" : 14.4378" +
                                "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("City with this name Liberec already exists in this country!")));
    }

    @Test
    void getCitiesShouldWorkTroughAllLayers() throws Exception {
        mockMvc.perform(get("/api/v1/city"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                                "[{" +
                                        "\"name\": \"Liberec\"," +
                                        "\"longitude\": 15.0543," +
                                        "\"latitude\": 50.7663" +
                                        "}," +
                                        "{" +
                                        "\"name\": \"Warsaw\"," +
                                        "\"longitude\": 21.0122," +
                                        "\"latitude\": 52.2297" +
                                        "}]"
                        )
                );
    }

    @Test
    void getCityInCountryShouldWorkTroughAllLayers() throws Exception {
        mockMvc.perform(get("/api/v1/country/Czechia/Liberec"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("Liberec"))
                .andExpect(jsonPath("longitude").value("15.0543"))
                .andExpect(jsonPath("latitude").value("50.7663"));
    }

    @Test
    void getCityShouldThrowExceptionWhenCountryDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/country/Czechiaa/Liberec")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\" : \"Praha\"," +
                                "\"longitude\" : 50.0755," +
                                "\"latitude\" : 14.4378" +
                                "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Czechiaa does not exist!")));
    }

    @Test
    void getCityShouldThrowExceptionWhenCityDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/country/Czechia/Liberecc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\" : \"Praha\"," +
                                "\"longitude\" : 50.0755," +
                                "\"latitude\" : 14.4378" +
                                "}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("City with name Liberecc does not exist!")));

    }

    @Test
    void deleteCityShouldWorkTroughAllLayers() throws Exception{
        mockMvc.perform(delete("/api/v1/country/Czechia/Liberec"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCityShouldThrowExceptionWhenCountryDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/v1/country/Czechiaa/Liberec"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Czechiaa does not exist!")));
    }

    @Test
    void deleteCityShouldThrowExceptionWhenCityDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/v1/country/Czechia/Liberecc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("City with name Liberecc does not exist!")));
    }

    @Test
    void updateCityShouldWorkTroughAllLayers() throws Exception{
        mockMvc.perform(put("/api/v1/country/Czechia/Liberec?longitude=15.0546&latitude=50.7664&name=Krakow&country=Poland"))
                .andExpect(status().isOk());
    }

    @Test
    void updateCityShouldThrowExceptionWhenCountryDoesNotExist() throws Exception {
        mockMvc.perform(put("/api/v1/country/Czechiaa/Liberec?longitude=0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Czechiaa does not exist!")));
    }

    @Test
    void updateCityShouldThrowExceptionWhenCityDoesNotExist() throws Exception {
        mockMvc.perform(put("/api/v1/country/Czechia/Liberecc?longitude=0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("City with name Liberecc does not exist!")));
    }

    @Test
    void updateCityShouldThrowExceptionWhenNewCountryDoesNotExist() throws Exception {
        mockMvc.perform(put("/api/v1/country/Czechia/Liberec?longitude=15.0546&latitude=50.7664&name=Krakow&country=Polandd"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Polandd does not exist!")));
    }
}