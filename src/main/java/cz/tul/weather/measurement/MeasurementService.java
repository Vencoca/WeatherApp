package cz.tul.weather.measurement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

}

