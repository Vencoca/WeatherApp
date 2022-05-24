package cz.tul.weather.country;

import cz.tul.weather.exception.ApiRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class CountryService {

    private final CountryRepository countryRepository;
    private String errorMessage;
    @Autowired
    public CountryService(CountryRepository countryRepository) {this.countryRepository = countryRepository;}

    public List<Country> getAllCountries() {return  countryRepository.findAll();}

    public Country getCountry(String countryName) {
        return countryRepository.findCountryByName(countryName).orElseThrow(() -> {
                errorMessage = "Country with name " + countryName + " does not exist!";
                log.warn(errorMessage);
                return new ApiRequestException(errorMessage);
        });
    }

    public void addNewCountry(Country country){
        Optional<Country> countryOptional = countryRepository.findCountryByName(country.getName());
        if (countryOptional.isPresent()){
            errorMessage = "Name taken";
            log.warn(errorMessage);
            throw new ApiRequestException(errorMessage);
        }
        if (country.getName() != null && country.getName().length() > 0){
            countryRepository.save(country);
        } else {
            errorMessage = "Name cant be empty";
            log.warn(errorMessage);
            throw (new ApiRequestException(errorMessage));
        }
    }

    @Transactional
    public void deleteCountry(String countryName) {
        Optional<Country> countryOptional = countryRepository.findCountryByName(countryName);
        if (countryOptional.isEmpty()){
            errorMessage = "Country with name " + countryName + " does not exist!";
            log.warn(errorMessage);
            throw new ApiRequestException(errorMessage);
        }
        countryRepository.deleteByName(countryName);
    }

    @Transactional
    public void deleteCountries(){
        countryRepository.deleteAll();
    }

    @Transactional
    public void updateCountry(String countryName, Country countryNew) {
        Country country = countryRepository.findCountryByName(countryName).orElseThrow(() -> {
            errorMessage = "Country with name " + countryName + " does not exist!";
            log.warn(errorMessage);
            return new ApiRequestException(errorMessage);
        });

        Optional<Country> countryOptional = countryRepository.findCountryByName(countryNew.getName());
        if (countryOptional.isPresent()){
            errorMessage = "Country with name " + countryNew.getName() + " already exist!";
            log.warn(errorMessage);
            throw new ApiRequestException("Country with name " + countryNew.getName() + " already exist!");
        }

        if (countryNew.getName() != null && countryNew.getName().length() > 0 && !Objects.equals(country.getName(),countryNew.getName())){
            country.setName(countryNew.getName());
        }
    }

    @Transactional
    public void addNewCountries(List<Country> countries) {
        Optional<Country> countryOptional;
        for(Country country: countries) {
            countryOptional = countryRepository.findCountryByName(country.getName());
            if (countryOptional.isPresent()) {
                errorMessage = "Name " + country.getName() + " already taken";
                log.warn(errorMessage);
                throw new ApiRequestException(errorMessage);
            }
            if (country.getName() != null && country.getName().length() > 0) {
                countryRepository.save(country);
            } else {
                errorMessage = "Name cant be empty";
                log.warn(errorMessage);
                throw (new ApiRequestException(errorMessage));
            }
        }
    }
}
