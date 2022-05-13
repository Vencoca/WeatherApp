package cz.tul.weather.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1")
public class CountryController {
    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService){this.countryService = countryService;}

    @PostMapping(path = "/country")
    public void registerNewCountry(@RequestBody Country country){countryService.addNewCountry(country);}

    @GetMapping("/country")
    public List<Country> getCountries(){return countryService.getCountries();}

    @GetMapping(path = "/country/{countryName}")
    public Country getCountry(@PathVariable("countryName") String countryName){return countryService.getCountry(countryName);}

    @PutMapping(path = "country/{countryName}")
    public void updateCountry(@PathVariable("countryName") String countryName, @RequestBody Country countryNew){countryService.updateCountry(countryName, countryNew);}

    @DeleteMapping(path = "/country/{countryName}")
    public void deleteCountry(@PathVariable("countryName") String countryName){countryService.deleteCountry(countryName);}

}
