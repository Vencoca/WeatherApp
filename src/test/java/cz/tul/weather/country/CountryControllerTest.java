package cz.tul.weather.country;

import cz.tul.weather.exception.ApiRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CountryController.class)
@ExtendWith(MockitoExtension.class)
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CountryService countryService;

    @Test
    void canRegisterNewCountry() throws Exception{
        mockMvc.perform(
                post("/api/v1/country/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Czechia\"}")
                )
                .andExpect(status().isOk());
    }

    @Test
    void RegisterNewCountryShouldThrowBadRequestExceptionWhenMissingBody() throws Exception{
        mockMvc.perform(post("/api/v1/country/"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void canRegisterNewCountries() throws  Exception{
        mockMvc.perform(
                post("/api/v1/countries/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[" +
                                "{\"name\": \"Czechia\"}," +
                                "{\"name\": \"Poland\"}" +
                                "]")
                )
                .andExpect(status().isOk());
    }

    @Test
    void RegisterNewCountriesShouldThrowBadRequestExceptionWhenMissingBody() throws Exception{
        mockMvc.perform(post("/api/v1/countries/"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCountries() throws Exception {
        //Given
        Country czechia = new Country("Czechia");
        Country poland = new Country("Poland");
        List<Country> countries = new ArrayList<>();
        countries.add(czechia);
        countries.add(poland);
        given(countryService.getAllCountries()).willReturn(countries);
        //When
        //Then
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
    void canGetCountry() throws Exception{
        //Given
        String countryName = "Czechia";
        Country country = new Country(countryName);
        given(countryService.getCountry(countryName)).willReturn(country);
        //When
        //Then
        mockMvc.perform(get("/api/v1/country/Czechia"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"name\": \"Czechia\"}"));
    }

    @Test

    void getCountryShouldThrowExceptionWhenCountryIsMissing() throws Exception{
        //Given
        String countryName = "Czechia";
        ApiRequestException apiRequestException = new ApiRequestException("Country with name " + countryName + " does not exist!");
        given(countryService.getCountry(countryName)).willThrow(apiRequestException);
        //When
        //Then
        mockMvc.perform(get("/api/v1/country/Czechia"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Country with name " + countryName + " does not exist!")));
    }


    @Test
    void canUpdateCountry() throws Exception{
        mockMvc.perform(put("/api/v1/country/Czechia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Poland\"}")
                )
                .andExpect(status().isOk());
    }

    @Test
    void updateCountryShouldThrowExceptionWhenMissingBody() throws Exception{
        mockMvc.perform(put("/api/v1/country/Czechia"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void deleteCountry() throws Exception {
        mockMvc.perform(delete("/api/v1/country/Czechia"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAllCountries() throws Exception{
        mockMvc.perform(delete("/api/v1/country/"))
                .andExpect(status().isOk());
    }
}