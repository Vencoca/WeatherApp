package cz.tul.weather.city;

import com.fasterxml.jackson.annotation.JsonBackReference;
import cz.tul.weather.country.Country;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@Getter @Setter @NoArgsConstructor
public class City {
    @Id
    @SequenceGenerator(
            name="city_sequence",
            sequenceName = "city_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "city_sequence"
    )
    private Long id;
    private String name;
    private Double longitude;
    private Double latitude;

    @ManyToOne()
    @JsonBackReference
    private Country country;

    public City(String name, Double longitude, Double latitude, Country country) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
    }
}

