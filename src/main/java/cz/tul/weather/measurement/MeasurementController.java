package cz.tul.weather.measurement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1")
public class MeasurementController {

    final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @GetMapping(path = "/country/{countryName}/{cityName}/history")
    public List<Measurement> getMeasurements(
            @PathVariable("countryName") String countryName,
            @PathVariable("cityName") String cityName
    ){
        return measurementService.getAllMeasurementForCity(cityName,countryName);
    }

    @GetMapping(path = "/country/{countryName}/{cityName}/weather")
    public Measurement getMeasurement(
            @PathVariable("countryName") String countryName,
            @PathVariable("cityName") String cityName
    ){
        return measurementService.getLastMeasurementForCity(cityName,countryName);
    }

    @GetMapping(path = "/country/{countryName}/{cityName}/avg")
    public List<Measurement> getAverage(
        @PathVariable("countryName") String countryName,
        @PathVariable("cityName") String cityName
    ){
        return measurementService.getAverageForCity(cityName,countryName);
    }
}
