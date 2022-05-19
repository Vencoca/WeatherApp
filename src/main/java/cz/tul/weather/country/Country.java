package cz.tul.weather.country;

import cz.tul.weather.city.City;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "country")
@Getter @Setter @NoArgsConstructor
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
}
