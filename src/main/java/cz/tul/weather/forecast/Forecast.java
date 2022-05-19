package cz.tul.weather.forecast;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Forecast {
    @Getter @Setter @NoArgsConstructor
    public class Sys{
        private Long sunrise;
        private Long sunset;
    }

    @Getter @Setter @NoArgsConstructor
    public class Main {
        private Double temp;
        private Double feels_like;
        private Long pressure;
        private Integer humidity;
    }

    @Getter @Setter @NoArgsConstructor
    public class Wind {
        private Double speed;
        private Integer deg;
    }

    private String name;
    private Main main;
    private Wind wind;
    private Sys sys;
    private Long dt;
    private Long timezone;
}