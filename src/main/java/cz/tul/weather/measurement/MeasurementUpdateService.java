package cz.tul.weather.measurement;

import cz.tul.weather.city.City;
import cz.tul.weather.city.CityRepository;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnExpression("${read-only.mode} == false and ${update.mode} == true")
public class MeasurementUpdateService {
    @Value("${weather.api.key}")
    private String apiKey;
    @Value("${weather.api.maxCalls}")
    private Integer maxApiCalls;

    private List<City> cities;
    private final MeasurementRepository measurementRepository;
    private final CityRepository cityRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public MeasurementUpdateService(MeasurementRepository measurementRepository, CityRepository cityRepository) {
        this.measurementRepository = measurementRepository;
        this.cityRepository = cityRepository;
        this.restTemplate = new RestTemplate();
        cities = new ArrayList<>();
    }

    @Scheduled(fixedRate = 60000)
    public void updateCities(){
        cities = cityRepository.findAll();
    }

    @Scheduled(fixedDelayString = "${weather.db.update}")
    public void UpdateDatabase() throws JSONException, InterruptedException {
        for (City city : cities) {
            Measurement measurement = new Measurement(restTemplate.getForObject("https://api.openweathermap.org/data/2.5/weather?appid="+apiKey+"&lat="+city.getLatitude()+"&lon="+city.getLongitude() +"&units=Metric", String.class));
            measurementRepository.save(measurement.getPoint(city.getCountry().getName(),city.getName()));
            Thread.sleep(60*1000/maxApiCalls);
        }
    }
}
