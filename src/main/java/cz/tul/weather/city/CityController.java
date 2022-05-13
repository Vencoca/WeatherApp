package cz.tul.weather.city;

import cz.tul.weather.country.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="api/v1")
public class CityController {

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService){
        this.cityService = cityService;
    }

    @PostMapping(path = "/{countryName}")
    public void registerNewCity(@PathVariable("countryName") String countryName, @RequestBody City city){
        cityService.addNewCity(countryName,city);
    }

    @GetMapping(path = "/city")
    public List<City> getCities() {return cityService.getCities();}

    @GetMapping(path= "/country/{countryName}/{cityName}")
    public City getCityInCountry(@PathVariable("countryName") String countryName, @PathVariable("cityName") String cityName){
        return cityService.getCityInCountry(countryName, cityName);
    }

    @DeleteMapping(path = "/country/{countryName}/{cityName}")
    public void deleteCity(@PathVariable("countryName") String countryName, @PathVariable("cityName") String cityName){
        cityService.deleteCity(countryName,cityName);
    }

    @PutMapping(path = "/country/{countryName}/{cityName}")
    public void updateCity(
            @PathVariable("countryName") String countryName,
            @PathVariable("cityName") String cityName,
            @RequestBody(required = false) City cityNew,
            @RequestParam(required = false) Country countryNew
    ) {
        cityService.updateCity(countryName,cityName,cityNew,countryNew);
    }
}
