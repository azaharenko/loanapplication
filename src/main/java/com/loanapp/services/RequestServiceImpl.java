package com.loanapp.services;

import com.loanapp.beans.FreeGeoIpResult;
import com.sun.jersey.api.client.ClientResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Service("requestService")
@PropertySource("file:${user.home}/.loanapplication/config.properties")
public class RequestServiceImpl implements RequestService {
    protected final static Logger log = LogManager.getLogger(RequestServiceImpl.class);

    @Value("${com.loanapp.number.session.second:2}")
    int numberOfSessionsPerSecond;

    @Value("${com.loanapp.default.country:LV}")
    String defaultCountry;


    @Autowired
    CountryResolverService countryResolverService;

    @Override
    public String getCountry(String ip) {
        if (org.springframework.util.StringUtils.isEmpty(ip)){
            log.info("Ip not detected, default country is LV");
            return defaultCountry;
        }

        ClientResponse response = countryResolverService.getCountry(ip);

        if (response == null) {
            log.info("Country detection service is unavailable");
            return defaultCountry;
        }

        if (response.getStatus() != 200) {
            log.info("Country detection service responded with error: " + response.getStatus());
            return defaultCountry;
        }
        FreeGeoIpResult geoIpResult = response.getEntity(FreeGeoIpResult.class);
        String country = geoIpResult.getCountry_code();
        return (country.isEmpty() ? defaultCountry : country);
    }

    private ConcurrentMap<String, PriorityQueue<Long>> countryRequestMap = new ConcurrentHashMap<>();

    @Override
    public boolean isSpamCompliant(String country) {
        synchronized (this) {
            PriorityQueue<Long> buff = countryRequestMap.getOrDefault(country, new PriorityQueue<>());
            Long now = System.currentTimeMillis();
            buff.add(now);
            while (buff.size() > numberOfSessionsPerSecond + 1) {
                buff.poll();
            }
            countryRequestMap.put(country, buff);
            if (buff.size() < numberOfSessionsPerSecond + 1) {
                return true;
            } else {
                Long firstItem = buff.peek();
                log.debug(country + ": Millis diff: " + (now-firstItem));
                return (now-firstItem) > 10000;
            }
        }
    }
}
