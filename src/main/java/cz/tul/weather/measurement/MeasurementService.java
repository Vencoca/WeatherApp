package cz.tul.weather.measurement;

import cz.tul.weather.city.CityRepository;
import cz.tul.weather.country.Country;
import cz.tul.weather.country.CountryRepository;
import cz.tul.weather.exception.ApiRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;

@Service
@Slf4j
public class MeasurementService {
    private final MeasurementRepository measurementRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private String errorMessage;

    @Autowired
    public MeasurementService(MeasurementRepository measurementRepository, CountryRepository countryRepository, CityRepository cityRepository) {
        this.measurementRepository = measurementRepository;
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
    }

    public List<Measurement> getAllMeasurementForCity(String cityName,String countryName){
        getCountryAndCheckCityFromRepository(cityName, countryName);
        return measurementRepository.findMeasurementsByCountryAndCity(cityName,countryName);
    }

    public List<Measurement> getAverageForCity(String cityName, String countryName) {
        getCountryAndCheckCityFromRepository(cityName, countryName);
        return measurementRepository.findAveragesByCountryAndCity(cityName,countryName);
    }

    public Measurement getLastMeasurementForCity(String cityName, String countryName) {
        getCountryAndCheckCityFromRepository(cityName, countryName);
        return  measurementRepository.findMeasurementByCountryAndCity(cityName,countryName);
    }

    public ByteArrayInputStream getMeasurementsForCityInCsv(String cityName, String countryName) {
        getCountryAndCheckCityFromRepository(cityName, countryName);
        List<Measurement> list = measurementRepository.findMeasurementsByCountryAndCity(cityName,countryName);
        StringBuilder out = new StringBuilder();
        for(Measurement measurement : list){
            out.append(measurement.toCsv()).append(System.lineSeparator());
        }
        return new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
    }

    public void addNewMeasurements(String cityName, String countryName, MultipartFile file) {
        getCountryAndCheckCityFromRepository(cityName, countryName);
        try {
            String csvFileContent = new String(file.getBytes());
            Scanner scanner = new Scanner(csvFileContent);
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String [] arguments = line.split(",");
                if (arguments.length != 7){
                    String message = "Too many arguments in line: " + line;
                    log.warn(message);
                    throw new ApiRequestException(message);
                }
                Measurement measurement = new Measurement();
                measurement.setTemp(Double.parseDouble(arguments[0]));
                measurement.setFeelsLike(Double.parseDouble(arguments[1]));
                measurement.setPressure(Long.parseLong(arguments[2]));
                measurement.setHumidity(Integer.parseInt(arguments[3]));
                measurement.setWindSpeed(Double.parseDouble(arguments[4]));
                measurement.setWindDeg(Integer.parseInt(arguments[5]));
                measurement.setTime(Instant.parse(arguments[6]));
                measurementRepository.save(measurement.getPoint(countryName,cityName));
            }
            scanner.close();
        } catch (IOException|NumberFormatException e) {
            String message = "Error cant parse file!";
            log.warn(message);
            throw new ApiRequestException(message);
        }

    }

    public void deleteAllMeasurementsForCity(String cityName, String countryName) {
        getCountryAndCheckCityFromRepository(cityName, countryName);
        measurementRepository.deleteAll(cityName,countryName);
    }

    private void getCountryAndCheckCityFromRepository(String cityName, String countryName) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> {
            errorMessage = "Country with name " + countryName + " does not exist!";
            log.warn(errorMessage);
            return new ApiRequestException(errorMessage);
        });
        cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> {
            errorMessage = "City with name " + cityName + " does not exist!";
            log.warn(errorMessage);
            return new ApiRequestException(errorMessage);
        });
    }
}

