package cz.tul.weather;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@EnableScheduling
public class WeatherApplication {
	@Value("${spring.influx.bucket}")
	private String bucket;
	@Value("${spring.influx.token}")
	private String token;
	@Value("${spring.influx.url}")
	private String url;
	@Value("${spring.influx.org}")
	private String org;

	public static void main(String[] args) {
		SpringApplication.run(WeatherApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public InfluxDBClient getInfluxDBClient(){return InfluxDBClientFactory.create(url, token.toCharArray(),org,bucket);}


	/*
	@Bean
	CommandLineRunner commandLineRunner(CountryRepository countryRepository, CityRespository cityRespository){
		return args -> {
			Country czechia = new Country(
					"Czechia"
			);
			Country poland = new Country(
					"Poland"
			);

			countryRepository.saveAll(
					List.of(czechia,poland)
			);

			cityRespository.save(new City("Gdańsk", 18.64637, 54.352051,poland));
			cityRespository.save(new City("Pruszcz Gdański", 18.64637, 54.352051,poland));

		};
	}*/
}
