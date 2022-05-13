package cz.tul.weather.country;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import cz.tul.weather.city.City;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Country")
public class Country {
    @Id
    @SequenceGenerator(
            name="country_sequence",
            sequenceName = "country_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "country_sequence"
    )
    private Long id;

    @Column(name = "name",unique = true, columnDefinition="VARCHAR(75)")
    private String name;

    @OneToMany(mappedBy = "country",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<City> Cities;

    public Country(String name) {
        this.name = name;
    }

    public Country() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<City> getCities() {
        return Cities;
    }

    public void setCities(Set<City> cities) {
        Cities = cities;
    }


}