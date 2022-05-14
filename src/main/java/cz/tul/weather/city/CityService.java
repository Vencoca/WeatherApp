package cz.tul.weather.city;

import cz.tul.weather.country.Country;
import cz.tul.weather.country.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CityService {

    private final CityRespository cityRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public CityService(CityRespository cityRespository, CountryRepository countryRepository) {
        this.cityRepository = cityRespository;
        this.countryRepository = countryRepository;
    }

    public List<City> getCities() {return cityRepository.findAll();}

    public void addNewCity(String countryName, City city){
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> new IllegalStateException(
                "Country with name " + countryName + " does not exist!"
        ));

        Optional<City> cityOptional = cityRepository.findCityByNameAndCountry(city.getName(), country);
        if (cityOptional.isPresent()){
            throw new IllegalStateException("City with this name already exists in this country!");
        }
        city.setCountry(country);
        cityRepository.save(city);
    }


    public City getCityInCountry(String countryName, String cityName) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> new IllegalStateException(
                "Country with name " + countryName + " does not exist!"
        ));
        return cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> new IllegalStateException(
                "City with name " + cityName + " does not exist!"
        ));
    }

    public void deleteCity(String countryName, String cityName) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> new IllegalStateException(
                "Country with name " + countryName + " does not exist!"
        ));
        City city = cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> new IllegalStateException(
                "City with name " + cityName + " does not exist!"
        ));
        cityRepository.deleteById(city.getId());
    }
    @Transactional
    public void updateCity(String countryName, String cityName, String nameNew, Double longitudeNew, Double latitudeNew, String countryNameNew) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> new IllegalStateException(
                "Country with name " + countryName + " does not exist!"
        ));
        City city = cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> new IllegalStateException(
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
            Country countryNew =  countryRepository.findCountryByName(countryNameNew).orElseThrow(() -> new IllegalStateException(
                    "Country with name " + countryNameNew + " does not exist!"
            ));
            if(!Objects.equals(city.getCountry(),countryNew)) {
                city.setCountry(countryNew);
            }
        }
    }
}
