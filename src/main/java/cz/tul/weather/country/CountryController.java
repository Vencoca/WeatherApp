package cz.tul.weather.country;

import cz.tul.weather.readOnlyAspect.ReadOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1")
public class CountryController {
    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService){this.countryService = countryService;}

    @PostMapping(path = "/country")
    @ReadOnly
    public void registerNewCountry(@RequestBody Country country){countryService.addNewCountry(country);}

    @PostMapping(path = "/countries")
    @ReadOnly
    public void registerNewCountries(@RequestBody List<Country> countries){countryService.addNewCountries(countries);}

    @GetMapping("/country")
    public List<Country> getCountries(){return countryService.getAllCountries();}

    @GetMapping(path = "/country/{countryName}")
    public Country getCountry(@PathVariable("countryName") String countryName){return countryService.getCountry(countryName);}

    @PutMapping(path = "/country/{countryName}")
    @ReadOnly
    public void updateCountry(@PathVariable("countryName") String countryName, @RequestBody Country countryNew){countryService.updateCountry(countryName, countryNew);}

    @DeleteMapping(path = "/country/{countryName}")
    @ReadOnly
    public void deleteCountry(@PathVariable("countryName") String countryName){countryService.deleteCountry(countryName);}

    @DeleteMapping(path = "/country")
    @ReadOnly
    public void deleteAllCountries(){countryService.deleteCountries();}
}
