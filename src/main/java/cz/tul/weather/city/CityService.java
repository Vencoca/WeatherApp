package cz.tul.weather.city;

import cz.tul.weather.country.Country;
import cz.tul.weather.country.CountryRepository;
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
public class CityService {

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private String errorMessage;

    @Autowired
    public CityService(CityRepository cityRepository, CountryRepository countryRepository) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    public List<City> getCities() {return cityRepository.findAll();}

    public void addNewCity(String countryName, City city){
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> {
            errorMessage = "Country with name " + countryName + " does not exist!";
            log.warn(errorMessage);
            return new ApiRequestException(errorMessage);
        });

        Optional<City> cityOptional = cityRepository.findCityByNameAndCountry(city.getName(), country);
        if (cityOptional.isPresent()){
            errorMessage = "City with this name" + city.getName() + " already exists in this country!";
            log.warn(errorMessage);
            throw new ApiRequestException(errorMessage);
        }
        city.setCountry(country);
        cityRepository.save(city);
    }


    public City getCityInCountry(String countryName, String cityName) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> {
            errorMessage = "Country with name " + countryName + " does not exist!";
            log.warn(errorMessage);
            return new ApiRequestException(errorMessage);
        });
        return cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> {
            errorMessage = "City with name " + cityName + " does not exist!";
            log.warn(errorMessage);
            return new ApiRequestException(errorMessage);
        });
    }

    public void deleteCity(String countryName, String cityName) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> {
            errorMessage = "Country with name " + countryName + " does not exist!";
            log.warn(errorMessage);
            return new ApiRequestException(errorMessage);
        });
        City city = cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> {
            errorMessage = "City with name " + cityName + " does not exist!";
            log.warn(errorMessage);
            return new ApiRequestException(errorMessage);
        });
        cityRepository.deleteById(city.getId());
    }
    @Transactional
    public void updateCity(String countryName, String cityName, String nameNew, Double longitudeNew, Double latitudeNew, String countryNameNew) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> {
            errorMessage = "Country with name " + countryName + " does not exist!";
            log.warn(errorMessage);
            return new ApiRequestException(errorMessage);
        });
        City city = cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> new ApiRequestException(
                "City with name " + cityName + " does not exist!"
        ));

        if (nameNew != null && nameNew.length()>0 && !Objects.equals(city.getName(),nameNew)){
            city.setName(nameNew);
        }

        if (longitudeNew != null && !Objects.equals(city.getLongitude(),longitudeNew)) {
            city.setLongitude(longitudeNew);
        }
        if (latitudeNew != null && !Objects.equals(city.getLatitude(),latitudeNew)){
            city.setLatitude(latitudeNew);
        }

        if (countryNameNew != null){
            Country countryNew =  countryRepository.findCountryByName(countryNameNew).orElseThrow(() -> {
                errorMessage = "Country with name " + countryNameNew + " does not exist!";
                log.warn(errorMessage);
                return new ApiRequestException(errorMessage);
            });
            if(!Objects.equals(city.getCountry(),countryNew)) {
                city.setCountry(countryNew);
            }
        }
    }
}
