package cz.tul.weather.measurement;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MeasurementRepository {
    @Value("${spring.influx.bucket}") private String bucket;
    @Value("${spring.influx.token}") private String token;
    @Value("${spring.influx.url}") private String url;
    @Value("${spring.influx.org}") private String org;

    private final WriteApiBlocking writeApiBlocking;
    private final QueryApi queryApi;

    public MeasurementRepository(
            @Value("${spring.influx.bucket}") String bucket,
            @Value("${spring.influx.token}") String token,
            @Value("${spring.influx.url}") String url,
            @Value("${spring.influx.org}") String org
    ) {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
        this.writeApiBlocking = influxDBClient.getWriteApiBlocking();
        this.queryApi = influxDBClient.getQueryApi();
    }

    public void save(Point point){
        writeApiBlocking.writePoint(point);
    }

    public List<Measurement> findMeasurementsByCountryAndCity(String cityName, String countryName){
        String flux = String.format( "from(bucket:\"%s\") " +
                        "|> range(start:0) " +
                        "|>filter(fn: (r) => r[\"_measurement\"] == \"Weather\") " +
                        "|>filter(fn: (r) => r[\"City\"] == \"%s\")" +
                        "|> filter(fn: (r) => r[\"Country\"] == \"%s\")" +
                        "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")"
                , bucket, cityName,countryName);

        List<Measurement> measurements= new ArrayList<>();
        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for(FluxRecord fluxRecord : records){
                Measurement measurement = new Measurement();
                measurement.setHumidity(Integer.parseInt(fluxRecord.getValueByKey("Humidity").toString()));
                measurement.setPressure(Long.parseLong(fluxRecord.getValueByKey("Pressure").toString()));
                measurement.setTemp(Double.parseDouble(fluxRecord.getValueByKey("Temperature").toString()));
                measurement.setFeelsLike(Double.parseDouble(fluxRecord.getValueByKey("Temperature feels like").toString()));
                measurement.setWindSpeed(Double.parseDouble(fluxRecord.getValueByKey("Wind speed").toString()));
                measurement.setWindDeg(Integer.parseInt(fluxRecord.getValueByKey("Wind degrees").toString()));
                measurement.setTime(fluxRecord.getTime().plus(2, ChronoUnit.HOURS));
                measurements.add(measurement);
            }
        }
        return measurements;
    }
}
