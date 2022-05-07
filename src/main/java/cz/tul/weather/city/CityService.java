package cz.tul.weather.city;

import cz.tul.weather.country.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CityService {

    private final CityRespository cityRepository;

    @Autowired
    public CityService(CityRespository cityRespository) {this.cityRepository = cityRespository;}

    public List<City> getCities() {return cityRepository.findAll();}

    public void addNewCity(City city){
        Optional<City> cityOptional = cityRepository.findCityByName(city.getName());
        if (cityOptional.isPresent()){
            throw new IllegalStateException("Name taken");
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
                "city with id " + cityId + " does not exists"
        ));
    }
}
