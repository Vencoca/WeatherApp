package cz.tul.weather.forecast;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import cz.tul.weather.city.City;
import cz.tul.weather.city.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

@Service
public class ForecastService {
    @Value("${weather.api.key}")
    private String apiKey;

    private RestTemplate restTemplate;
    private final InfluxDBClient client;
    private final WriteApiBlocking writeApi;
    private final CityRepository cityRepository;
    private List<City> cities;
    private Integer count;

    @Autowired
    public ForecastService(RestTemplate restTemplate, InfluxDBClient client, CityRepository cityRepository) {
        this.restTemplate = restTemplate;
        this.client = client;
        this.cityRepository = cityRepository;
        this.writeApi = this.client.getWriteApiBlocking();
        count = 0;
    }
    @Scheduled(fixedRate = 60000)
    public void updateCities(){
        cities = cityRepository.findAll();
    }

    @Scheduled(fixedRateString = "${weather.db.update}")
    public void write(){
        if (cities != null) {
            count = count + 1;
            for (City city : cities) {
                Forecast forecast = restTemplate.getForObject("https://api.openweathermap.org/data/2.5/weather?appid="+apiKey+"&lat="+city.getLatitude()+"&lon="+city.getLongitude() +"&units=Metric",Forecast.class);

                Point point = Point.measurement("Weather")
                        .addTag("Country", city.getCountry().getName())
                        .addTag("City", city.getName())
                        .addField("Temperature", forecast.getMain().getTemp())
                        .addField("Temperature feels like", forecast.getMain().getFeels_like())
                        .addField("Pressure", forecast.getMain().getPressure())
                        .addField("Humidity", forecast.getMain().getHumidity())
                        .addField("Wind speed", forecast.getWind().getSpeed())
                        .addField("Wind degrees", forecast.getWind().getDeg())
                        .time(Instant.now().toEpochMilli(), WritePrecision.MS);
                writeApi.writePoint(point);
            }
        }
    }
}
