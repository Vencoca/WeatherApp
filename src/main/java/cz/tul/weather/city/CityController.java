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
    public CityController(CityService cityService){this.cityService = cityService;}

    @GetMapping(path = "/city")
    public List<City> getCities() {return cityService.getCities();}

    @GetMapping(path="/city/{cityId}")
    public City getCity(@PathVariable("cityId") Long cityId){
        return cityService.getCity(cityId);
    }

    @PostMapping(path = "/city")
    public void registerNewCity(@RequestBody City city){
        cityService.addNewCity(city);
    }

    @DeleteMapping(path = "/city/{cityId}")
    public void deleteCity(@PathVariable("cityId") Long cityId){
        cityService.deleteCity(cityId);
    }

    @PutMapping(path = "/city/{cityId}")
    public void updateCity(
            @PathVariable("cityId") Long cityId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Country country
    ) {
        cityService.updateCity(cityId,name,longitude,latitude,country);
    }
}
