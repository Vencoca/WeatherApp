package cz.tul.weather.measurement;

import cz.tul.weather.city.CityRepository;
import cz.tul.weather.country.Country;
import cz.tul.weather.country.CountryRepository;
import cz.tul.weather.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Service
public class MeasurementService {
    private final MeasurementRepository measurementRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;

    @Autowired
    public MeasurementService(MeasurementRepository measurementRepository, CountryRepository countryRepository, CityRepository cityRepository) {
        this.measurementRepository = measurementRepository;
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
    }

    public List<Measurement> getAllMeasurementForCity(String cityName,String countryName){
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> new ApiRequestException(
                "Country with name " + countryName + " does not exist!"
        ));
        cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> new ApiRequestException(
                "City with name " + cityName + " does not exist!"
        ));
       return measurementRepository.findMeasurementsByCountryAndCity(cityName,countryName);
    }

    public List<Measurement> getAverageForCity(String cityName, String countryName) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> new ApiRequestException(
                "Country with name " + countryName + " does not exist!"
        ));
        cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> new ApiRequestException(
                "City with name " + cityName + " does not exist!"
        ));
        return measurementRepository.findAveragesByCountryAndCity(cityName,countryName);
    }

    public Measurement getLastMeasurementForCity(String cityName, String countryName) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> new ApiRequestException(
                "Country with name " + countryName + " does not exist!"
        ));
        cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> new ApiRequestException(
                "City with name " + cityName + " does not exist!"
        ));
        return  measurementRepository.findMeasurementByCountryAndCity(cityName,countryName);
    }

    public ByteArrayInputStream getMeasurementsForCityInCsv(String cityName, String countryName) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> new ApiRequestException(
                "Country with name " + countryName + " does not exist!"
        ));
        cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> new ApiRequestException(
                "City with name " + cityName + " does not exist!"
        ));
        List<Measurement> list = measurementRepository.findMeasurementsByCountryAndCity(cityName,countryName);
        String out = "";
        for(Measurement measurement : list){
            out = out + measurement.toCsv() + System.lineSeparator();
        }
        return new ByteArrayInputStream(out.getBytes(StandardCharsets.UTF_8));
    }

    public void addNewMeasurements(String cityName, String countryName, MultipartFile file) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> new ApiRequestException(
                "Country with name " + countryName + " does not exist!"
        ));
        cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> new ApiRequestException(
                "City with name " + cityName + " does not exist!"
        ));
        try {
            String csvFileContent = new String(file.getBytes());
            Scanner scanner = new Scanner(csvFileContent);
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String [] arguments = line.split(",");
                if (arguments.length != 7){
                    throw new ApiRequestException("Too many arguments in line: " + line);
                }
                Measurement measurement = new Measurement();
                measurement.setTemp(Double.parseDouble(arguments[0]));
                measurement.setFeelsLike(Double.parseDouble(arguments[1]));
                measurement.setPressure(Long.parseLong(arguments[2]));
                measurement.setHumidity(Integer.parseInt(arguments[3]));
                measurement.setWindSpeed(Double.parseDouble(arguments[4]));
                measurement.setWindDeg(Integer.parseInt(arguments[5]));
                measurement.setTime(Instant.parse(arguments[6]));

                if(!Objects.equals(measurement.toCsv(), line)) {
                    throw new ApiRequestException("Error in converting line to object | line: " + line);
                }
                measurementRepository.save(measurement.getPoint(countryName,cityName));
            }
            scanner.close();
        } catch (IOException|NumberFormatException e) {
            throw new ApiRequestException("Error cant parse file!");
        }

    }

    public void deleteAllMeasurementsForCity(String cityName, String countryName) {
        Country country =  countryRepository.findCountryByName(countryName).orElseThrow(() -> new ApiRequestException(
                "Country with name " + countryName + " does not exist!"
        ));
        cityRepository.findCityByNameAndCountry(cityName,country).orElseThrow(() -> new ApiRequestException(
                "City with name " + cityName + " does not exist!"
        ));
        measurementRepository.deleteAll(cityName,countryName);
    }
}

