package cz.tul.weather.forecast;

import cz.tul.weather.city.City;
import cz.tul.weather.city.CityRepository;
import cz.tul.weather.country.Country;
import cz.tul.weather.country.CountryRepository;
import cz.tul.weather.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1")
public class ForecastController {

    @Value("${weather.api.key}")
    private String apiKey;

    private RestTemplate restTemplate;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public ForecastController(RestTemplate restTemplate, CityRepository cityRepository, CountryRepository countryRepository) {
        this.restTemplate = restTemplate;
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    @GetMapping(path = "/country/{countryName}/{cityName}/weather")
    public Forecast getForecast(
            @PathVariable("countryName") String countryName,
            @PathVariable("cityName") String cityName
    ){
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> new ApiRequestException(
                "Country with name " + countryName + " does not exist!"
        ));
        City city = cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> new ApiRequestException(
                "City with name " + cityName + " does not exist!"
        ));
        Forecast forecast = restTemplate.getForObject("https://api.openweathermap.org/data/2.5/weather?appid="+apiKey+"&lat="+city.getLatitude()+"&lon="+city.getLongitude() +"&units=Metric",Forecast.class);
        return forecast;
    }


}
