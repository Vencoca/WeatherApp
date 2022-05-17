package cz.tul.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class WeatherApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

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
