package cz.tul.weather.country;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryIntegrationTest {

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private MockMvc mockMvc;

    private final Country czechia = new Country("Czechia");
    private final Country poland = new Country("Poland");

    @BeforeEach
    void setUp() {
        countryRepository.save(czechia);
        countryRepository.save(poland);
    }

    @AfterEach
    void tearDown() {
        countryRepository.deleteAll();
    }

    @Test
    public void registerNewCountriesShouldWorkTroughAllLayers() throws Exception{
        mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[" +
                                "{\"name\": \"Germany\"}," +
                                "{\"name\": \"France\"}" +
                                "]")
                )
                .andExpect(status().isOk());
    }

    @Test
    public void registerNewCountriesShouldThrowExceptionWhenNameIsDuplicate() throws Exception{
        mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[" +
                                "{\"name\": \"Germany\"}," +
                                "{\"name\": \"Czechia\"}" +
                                "]")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Name Czechia already taken")));
    }

    @Test
    public void registerNewCountriesShouldThrowExceptionWhenNameIsEmpty() throws Exception{
        mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[" +
                                "{\"name\": \"Germany\"}," +
                                "{\"name\": \"\"}" +
                                "]")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Name cant be empty")));
    }


    @Test
    public void registerNewCountryShouldWorkTroughAllLayers() throws Exception{
        mockMvc.perform(post("/api/v1/country/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\": \"Germany\"}")
                )
                .andExpect(status().isOk());
    }

    @Test
    public void registerNewCountryShouldThrowExceptionWhenNameIsDuplicate() throws Exception{
        mockMvc.perform(post("/api/v1/country/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Czechia\"}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Name taken")));
    }

    @Test
    public void registerNewCountryShouldThrowExceptionWhenNameIsEmpty() throws Exception{
        mockMvc.perform(post("/api/v1/country/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Name cant be empty")));
    }


    @Test
    void getCountriesShouldWorkTroughAllLayers() throws  Exception{
        mockMvc.perform(get("/api/v1/country"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        "[" +
                                "{\"name\": \"Czechia\"}," +
                                "{\"name\": \"Poland\"}" +
                                "]"));
    }


    @Test
    void getCountryShouldWorkTroughAllLayers() throws  Exception{
        mockMvc.perform(get("/api/v1/country/Czechia"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"name\": \"Czechia\"}"));
    }


    @Test
    public void getCountryShouldThrowExceptionWhenNameDoesNotExist() throws Exception{
        mockMvc.perform(get("/api/v1/country/Czechiaa"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Czechiaa does not exist!")));
    }

    @Test
    void updateCountryShouldWorkTroughAllLayers() throws  Exception{
        mockMvc.perform(put("/api/v1/country/Czechia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Germany\"}")
                )
                .andExpect(status().isOk());
    }

    @Test
    public void updateCountryShouldThrowExceptionWhenNameDoesNotExist() throws Exception{
        mockMvc.perform(put("/api/v1/country/Germany")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Czechia\"}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Germany does not exist!")));
    }

    @Test
    public void updateCountryShouldThrowExceptionWhenNewNameAlreadyExists() throws Exception{
        mockMvc.perform(put("/api/v1/country/Poland")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Czechia\"}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Czechia already exist!")));
    }

    @Test
    void deleteCountryShouldWorkTroughAllLayers() throws  Exception{
        mockMvc.perform(delete("/api/v1/country/Czechia"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCountryShouldThrowExceptionWhenCountryDoesNotExist() throws Exception{
        mockMvc.perform(delete("/api/v1/country/Czechiaa"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name Czechiaa does not exist!")));
    }

    @Test
    void deleteAllCountriesShouldWorkTroughAllLayers() throws  Exception{
        mockMvc.perform(delete("/api/v1/country/"))
                .andExpect(status().isOk());
    }


}
