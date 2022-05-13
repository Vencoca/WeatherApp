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
        Optional<City> cityOptional = cityRepository.findCityByNameAndCountry(city.getName(), city.getCountry());
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
                "City with name " + countryName + " does not exist!"
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
    public void updateCity(String countryName, String cityName, City cityNew, Country countryNew) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> new IllegalStateException(
                "UpdateCity: Country with name " + countryName + " does not exist!"
        ));
        City city = cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> new IllegalStateException(
                "UpdateCity: City with name " + cityName + " does not exist!"
        ));

        if (cityNew.getName() != null && cityNew.getName().length()>0 && !Objects.equals(city.getName(),cityNew.getName())){
            city.setName(cityNew.getName());
        }

        if (cityNew.getLongitude() != null && !Objects.equals(city.getLongitude(),cityNew.getLongitude())) {
            city.setLongitude(cityNew.getLongitude());
        }
        if (cityNew.getLatitude() != null && !Objects.equals(city.getLatitude(),cityNew.getLatitude())){
            city.setLatitude(cityNew.getLatitude());
        }

        if (countryNew != null && !Objects.equals(city.getCountry(),countryNew)){
            city.setCountry(countryNew);
        }
    }
}
