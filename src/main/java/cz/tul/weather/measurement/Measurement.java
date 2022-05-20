package cz.tul.weather.measurement;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;

@Getter @Setter @NoArgsConstructor
public class Measurement {
    private Double temp;
    private Double feelsLike;
    private Long pressure;
    private Integer humidity;
    private Double windSpeed;
    private Integer windDeg;
    private Instant time;

    public Measurement(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        this.time = Instant.ofEpochSecond(jsonObject.getInt("dt"));
        JSONObject main = jsonObject.getJSONObject("main");
        this.temp = main.getDouble("temp");
        this.feelsLike = main.getDouble("feels_like");
        this.pressure = main.getLong("pressure");
        this.humidity = main.getInt("humidity");
        JSONObject wind = jsonObject.getJSONObject("wind");
        this.windSpeed = wind.getDouble("speed");
        this.windDeg = wind.getInt("deg");
    }

    public Point getPoint(String countryName, String cityName){
        Point point = new Point("Weather");
        point.addTag("Country", countryName)
            .addTag("City", cityName)
            .addField("Temperature", this.temp)
            .addField("Temperature feels like", this.feelsLike)
            .addField("Pressure", this.pressure)
            .addField("Humidity", this.humidity)
            .addField("Wind speed", this.windSpeed)
            .addField("Wind degrees", this.windDeg)
            .time(time.toEpochMilli(), WritePrecision.MS);
        return point;
    }

    public String toCsv(){
        String separator = ",";
        return  temp + separator +
                feelsLike + separator +
                pressure + separator +
                humidity + separator +
                windSpeed + separator +
                windDeg + separator +
                time;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "temp=" + temp +
                ", feelsLike=" + feelsLike +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", windSpeed=" + windSpeed +
                ", windDeg=" + windDeg +
                ", time=" + time +
                '}';
    }
}
