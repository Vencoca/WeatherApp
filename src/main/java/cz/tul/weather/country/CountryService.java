package cz.tul.weather.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {

    private final CountryRepository countryRepository;
    @Autowired
    public CountryService(CountryRepository countryRepository) {this.countryRepository = countryRepository;}

    public List<Country> getCountries() {return  countryRepository.findAll();}

    public void addNewCountry(Country country){
        Optional<Country> countryOptional = countryRepository.findCountryByName(country.getName());
        if (countryOptional.isPresent()){
            throw new IllegalStateException("Name taken");
        }
        countryRepository.save(country);
    }
}
