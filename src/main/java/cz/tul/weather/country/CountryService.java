package cz.tul.weather.country;

import cz.tul.weather.city.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CountryService {

    private final CountryRepository countryRepository;
    @Autowired
    public CountryService(CountryRepository countryRepository) {this.countryRepository = countryRepository;}

    public List<Country> getCountries() {return  countryRepository.findAll();}

    public Country getCountry(String countryName) {
        return countryRepository.findCountryByName(countryName).orElseThrow(() -> new IllegalStateException(
                "Country with name " + countryName + " does not exists!"
        ));
    }

    public void addNewCountry(Country country){
        Optional<Country> countryOptional = countryRepository.findCountryByName(country.getName());
        if (countryOptional.isPresent()){
            throw new IllegalStateException("Name taken");
        }
        countryRepository.save(country);
    }

    @Transactional
    public void deleteCountry(String countryName) {
        Optional<Country> countryOptional = countryRepository.findCountryByName(countryName);
        if (countryOptional.isEmpty()){
            throw new IllegalStateException("Country with name " + countryName + " does not exist!");
        }
        countryRepository.deleteByName(countryName);
    }

    @Transactional
    public void updateCountry(String countryName, Country countryNew) {
        Country country = countryRepository.findCountryByName(countryName).orElseThrow(() -> new IllegalStateException(
                "Country with name " + countryName + " does not exist!"
        ));

        if (countryNew.getName() != null && countryNew.getName().length() > 0 && !Objects.equals(country.getName(),countryNew.getName())){
            country.setName(countryNew.getName());
        }
    }
}
