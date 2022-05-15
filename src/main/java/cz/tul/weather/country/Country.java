package cz.tul.weather.country;

import cz.tul.weather.city.City;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "country")
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

    @Column(unique = true, columnDefinition="VARCHAR(75)")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
