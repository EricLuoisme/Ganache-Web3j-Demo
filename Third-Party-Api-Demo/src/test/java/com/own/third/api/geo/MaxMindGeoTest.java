package com.own.third.api.geo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Paths;

/**
 * @author Roylic
 * 2023/8/17
 */
public class MaxMindGeoTest {

    private final static ObjectMapper om = new ObjectMapper();

    @Test
    public void ipResolution_Country() {
        String ip = "101.203.168.2";
        File absoluteFilePath = Paths.get("src", "main", "resources").toFile().getAbsoluteFile();
        File file = new File(absoluteFilePath + "/GeoLite2-Country.mmdb");
        try {
            DatabaseReader dbReader = new DatabaseReader.Builder(file).build();
            CountryResponse country = dbReader.country(InetAddress.getByName(ip));
            System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(country.getCountry()));
        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ipResolution_City() {
        String ip = "101.203.168.2";
        File absoluteFilePath = Paths.get("src", "main", "resources").toFile().getAbsoluteFile();
        File file = new File(absoluteFilePath + "/GeoLite2-City.mmdb");
        try {
            DatabaseReader dbReader = new DatabaseReader.Builder(file).build();
            CityResponse city = dbReader.city(InetAddress.getByName(ip));
            System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(city.getCity()));
        } catch (IOException | GeoIp2Exception e) {
            e.printStackTrace();
        }

    }
}
