package cz.tul.weather;

import cz.tul.weather.country.Country;
import cz.tul.weather.country.CountryRepository;
import org.assertj.core.api.Assertions;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
@SpringBootTest
public class WeatherApplicationTests {

	@Autowired
	ApplicationContext ctx;
	@Autowired
	CountryRepository repository;

	@Test
	public void testContextLoads() throws Exception {
		assertNotNull(this.ctx);
	}



	@Test
	public void SaveCountry() {
		Country poland = new Country(
				"Poland"
		);
		repository.save(poland);
		Assertions.assertThat(poland.getId()).isGreaterThan(0);
	}
}
