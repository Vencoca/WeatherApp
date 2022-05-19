package cz.tul.weather.measurement;


import cz.tul.weather.city.City;
import cz.tul.weather.city.CityRepository;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MeasurementService {
    @Value("${weather.api.key}")
    private String apiKey;
    private List<City> cities;
    private final RestTemplate restTemplate;
    private final MeasurementRepository measurementRepository;
    private final CityRepository cityRepository;

    @Autowired
    public MeasurementService(MeasurementRepository measurementRepository, CityRepository cityRepository) {
        this.measurementRepository = measurementRepository;
        this.cityRepository = cityRepository;
        this.restTemplate = new RestTemplate();
    }

    public List<Measurement> getAllMeasurementForCity(String cityName,String countryName){
       return measurementRepository.findMeasurementsByCountryAndCity(cityName,countryName);
    }

    @Scheduled(fixedRate = 60000)
    public void updateCities(){
        cities = cityRepository.findAll();
    }

    @Scheduled(fixedRateString = "${weather.db.update}")
    public void UpdateDatabase() throws JSONException {
        if (cities != null) {
            for (City city : cities) {
                Measurement measurement = new Measurement(restTemplate.getForObject("https://api.openweathermap.org/data/2.5/weather?appid="+apiKey+"&lat="+city.getLatitude()+"&lon="+city.getLongitude() +"&units=Metric", String.class));
                measurementRepository.save(measurement.getPoint(city.getCountry().getName(),city.getName()));
            }
        }
    }
}
