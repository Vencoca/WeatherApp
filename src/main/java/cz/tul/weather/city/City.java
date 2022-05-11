package cz.tul.weather.city;

import com.fasterxml.jackson.annotation.JsonBackReference;
import cz.tul.weather.country.Country;
import cz.tul.weather.country.CountryRepository;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Table(name = "City")
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
    private double longitude;
    private double latitude;

    @ManyToOne()
    @JsonBackReference
    private Country country;

    public City() {
    }

    public City(String name, Double longitude, Double latitude, Country country) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
    }

    public City(Long id, String name, Double longitude, Double latitude, Country country) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", country='" + country + '\'' +
                '}';
    }
}

