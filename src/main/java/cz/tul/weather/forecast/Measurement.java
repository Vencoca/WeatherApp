package cz.tul.weather.forecast;

import com.influxdb.annotations.Column;

import java.time.Instant;

@com.influxdb.annotations.Measurement(name = "Weather")
public class Measurement {
    @Column(name = "time")
    private Instant time;
    @Column(name = "Temperature")
    private Double temp;
    @Column(name = "Temperature feels like")
    private Double feels_like;
    @Column(name = "Pressure")
    private Long pressure;
    @Column(name = "Humidity")
    private Integer humidity;
    @Column(name = "Wind speed")
    private Double speed;
    @Column(name = "Wind degrees")
    private Integer deg;
}
