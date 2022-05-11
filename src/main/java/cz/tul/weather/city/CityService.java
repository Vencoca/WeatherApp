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

    public void addNewCity(City city){
        Optional<City> cityOptional = cityRepository.findCityByNameAndCountry(city.getName(), city.getCountry());
        if (cityOptional.isPresent()){
            throw new IllegalStateException("City with this name exist this country");
        }
        cityRepository.save(city);
    }

    public void deleteCity(Long cityId){
        boolean exists = cityRepository.existsById(cityId);
        if (!exists){
            throw new IllegalStateException(
                    "city with id" + cityId + "does not exists");
        }
        cityRepository.deleteById(cityId);
    }

    @Transactional
    public void updateCity(Long cityId, String name, Double longitude, Double latitude, Country country){
        City city = cityRepository.findById(cityId).orElseThrow(() -> new IllegalStateException(
                "city with id " + cityId + "does not exists"
        ));

        if (name != null && name.length()>0 && !Objects.equals(city.getName(),name)){
            city.setName(name);
        }

        if (longitude != null && !Objects.equals(city.getLongitude(),longitude)) {
            city.setLongitude(longitude);
        }
        if (latitude != null && !Objects.equals(city.getLatitude(),latitude)){
            city.setLatitude(latitude);
        }

        if (country != null && !Objects.equals(city.getCountry(),country)){
            city.setCountry(country);
        }
    }

    public City getCity(Long cityId) {
        return cityRepository.findById(cityId).orElseThrow(() -> new IllegalStateException(
                "city with id " + cityId + " does not exist"
        ));
    }

    public City getCityInCountry(String countryName, String cityName) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> new IllegalStateException(
                "Country with name " + countryName + " does not exists!"
        ));
        return cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> new IllegalStateException(
                "City with name " + countryName + " does not exists"
        ));
    }
}
