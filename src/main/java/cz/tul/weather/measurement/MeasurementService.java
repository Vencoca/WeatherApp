package cz.tul.weather.measurement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class MeasurementService {
    private final MeasurementRepository measurementRepository;


    @Autowired
    public MeasurementService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public List<Measurement> getAllMeasurementForCity(String cityName,String countryName){
       return measurementRepository.findMeasurementsByCountryAndCity(cityName,countryName);
    }

    public List<Measurement> getAverageForCity(String cityName, String countryName) {
        return measurementRepository.findAveragesByCountryAndCity(cityName,countryName);
    }

    public Measurement getLastMeasurementForCity(String cityName, String countryName) {
        return  measurementRepository.findMeasurementByCountryAndCity(cityName,countryName);
    }


    public ByteArrayInputStream getMeasurementsForCityInCsv(String cityName, String countryName) {
        List<Measurement> list = measurementRepository.findMeasurementsByCountryAndCity(cityName,countryName);
        String out = "";
        for(Measurement measurement : list){
            out = out + measurement.toCsv() + System.lineSeparator();
        }
        return new ByteArrayInputStream(out.getBytes(StandardCharsets.UTF_8));
    }
}

