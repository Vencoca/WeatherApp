package cz.tul.weather.country;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/country")
public class CountryController {
    private CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService){this.countryService = countryService;}

    @GetMapping
    public List<Country> getCountries(){return countryService.getCountries();}

}
