package cz.tul.weather.measurement;

import com.influxdb.client.*;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.BucketRetentionRules;
import com.influxdb.client.domain.DeletePredicateRequest;
import com.influxdb.client.domain.Query;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MeasurementRepository {
    private String bucket;
    private String token;
    private String url;
    private String org;

    private final WriteApiBlocking writeApiBlocking;
    private final QueryApi queryApi;
    private final BucketsApi bucketsApi;
    private final DeleteApi deleteApi;
    private final Integer retention;

    public MeasurementRepository(
            @Value("${spring.influx.bucket}") String bucket,
            @Value("${spring.influx.token}") String token,
            @Value("${spring.influx.url}") String url,
            @Value("${spring.influx.org}") String org,
            @Value("${spring.influx.retention}") Integer retention,
            @Value("${spring.influx.shardgroupduration}") Long shardGroupDuration
    ) {
        this.bucket = bucket;
        this.token = token;
        this.url = url;
        this.org = org;
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
        this.writeApiBlocking = influxDBClient.getWriteApiBlocking();
        this.queryApi = influxDBClient.getQueryApi();
        this.bucketsApi = influxDBClient.getBucketsApi();
        this.deleteApi = influxDBClient.getDeleteApi();
        this.retention = retention;
        setRetentionRules(retention,shardGroupDuration);
    }

    public void save(Point point){
        writeApiBlocking.writePoint(point);
    }


    public List<Measurement> findMeasurementsByCountryAndCity(String cityName, String countryName){
        String flux = String.format( "from(bucket:\"%s\") " +
                        "|> range(start:0) " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"Weather\") " +
                        "|> filter(fn: (r) => r[\"City\"] == \"%s\")" +
                        "|> filter(fn: (r) => r[\"Country\"] == \"%s\")" +
                        "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")"
                , bucket, cityName,countryName);

        return executeQuery(flux);
    }

    public void setRetentionRules(Integer retention, Long shardGroupDuration){
        BucketRetentionRules rules = new BucketRetentionRules();
        rules.setEverySeconds(3600*24*retention);
        rules.setShardGroupDurationSeconds(3600L*24*shardGroupDuration);
        List<BucketRetentionRules> rulesList = new ArrayList<>();
        rulesList.add(rules);
        Bucket bucket = bucketsApi.findBucketByName("weather");
        bucket.setRetentionRules(rulesList);
        bucketsApi.updateBucket(bucket);
    }

    public Measurement findMeasurementByCountryAndCity(String cityName, String countryName){
        String flux = String.format( "from(bucket:\"%s\") " +
                        "|> range(start:0) " +
                        "|> last() " +
                        "|> filter(fn: (r) => r[\"_measurement\"] == \"Weather\") " +
                        "|> filter(fn: (r) => r[\"City\"] == \"%s\")" +
                        "|> filter(fn: (r) => r[\"Country\"] == \"%s\")" +
                        "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")"
                , bucket, cityName,countryName);
        List<Measurement> measurements =  executeQuery(flux);
        if (measurements.isEmpty()) {
            return null;
        } else {
            return measurements.get(0);
        }
    }

    public List<Measurement> findAveragesByCountryAndCity(String cityName, String countryName) {
        String fluxPrototype = "from(bucket:\"%s\") " +
                "|> range(start: -%s, stop: now()) " +
                "|>filter(fn: (r) => r[\"_measurement\"] == \"Weather\") " +
                "|>filter(fn: (r) => r[\"City\"] == \"%s\")" +
                "|> filter(fn: (r) => r[\"Country\"] == \"%s\")" +
                "|> mean()"+
                "|> rename(columns: {\"_start\": \"_time\"})" +
                "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")";

        String flux = String.format(fluxPrototype, bucket, "14d", cityName,countryName);
        List<Measurement> measurements =  executeQuery(flux);
        flux = String.format(fluxPrototype, bucket, "7d", cityName,countryName);
        measurements.addAll(executeQuery(flux));
        flux = String.format(fluxPrototype, bucket, "1d", cityName,countryName);
        measurements.addAll(executeQuery(flux));
        return measurements;
    }

    private List<Measurement> executeQuery(String flux){
        List<Measurement> measurements= new ArrayList<>();
        List<FluxTable> tables = queryApi.query(flux);
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for(FluxRecord fluxRecord : records){
                Measurement measurement = new Measurement();
                measurement.setHumidity((int) Double.parseDouble(fluxRecord.getValueByKey("Humidity").toString()));
                measurement.setPressure((long) Double.parseDouble(fluxRecord.getValueByKey("Pressure").toString()));
                measurement.setTemp(Double.parseDouble(fluxRecord.getValueByKey("Temperature").toString()));
                measurement.setFeelsLike(Double.parseDouble(fluxRecord.getValueByKey("Temperature feels like").toString()));
                measurement.setWindSpeed(Double.parseDouble(fluxRecord.getValueByKey("Wind speed").toString()));
                measurement.setWindDeg((int) Double.parseDouble(fluxRecord.getValueByKey("Wind degrees").toString()));
                measurement.setTime(fluxRecord.getTime().plus(2, ChronoUnit.HOURS));
                measurements.add(measurement);
            }
        }
        return measurements;
    }

    public void deleteAll(String cityName, String countryName) {
        DeletePredicateRequest predicateRequest = new DeletePredicateRequest();
        predicateRequest.setPredicate("Country=\""+countryName+"\" AND City=\""+cityName+"\"");
        predicateRequest.setStart(OffsetDateTime.now().minus(retention, ChronoUnit.DAYS));
        predicateRequest.setStop(OffsetDateTime.now());
        deleteApi.delete(predicateRequest,bucket,org);
    }
}
