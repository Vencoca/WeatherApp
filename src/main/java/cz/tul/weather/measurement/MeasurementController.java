package cz.tul.weather.measurement;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping(path = "/country/{countryName}/{cityName}/history/csv")
    public ResponseEntity<InputStreamResource> getCsv(
            @PathVariable("countryName") String countryName,
            @PathVariable("cityName") String cityName
    ){
        String filename = countryName + "_" + cityName + ".csv";
        InputStreamResource resource = new InputStreamResource(
                measurementService.getMeasurementsForCityInCsv(cityName,countryName)
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);

    }

    @PostMapping(path = "/country/{countryName}/{cityName}/history/csv")
    public void uploadCsv(
            @PathVariable("countryName") String countryName,
            @PathVariable("cityName") String cityName,
            @RequestParam("file") MultipartFile file
    ){
        measurementService.addNewMeasurements(cityName,countryName,file);
    }

    @DeleteMapping(path = "/country/{countryName}/{cityName}/history/")
    public void deleteAllMeasurements(
            @PathVariable("countryName") String countryName,
            @PathVariable("cityName") String cityName
    ){
        measurementService.deleteAllMeasurementsForCity(cityName,countryName);
    }

}
