package cz.tul.weather.country;

import cz.tul.weather.city.City;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Country")
public class Country {
    @Id
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
