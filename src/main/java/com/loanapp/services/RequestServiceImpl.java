package com.loanapp.services;

import com.loanapp.beans.FreeGeoIpResult;
import com.loanapp.utils.Constants;
import com.sun.jersey.api.client.ClientResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Service
public class RequestServiceImpl implements RequestService {
    protected final static Logger log = LogManager.getLogger(RequestServiceImpl.class);

    @Autowired
    CountryResolverService countryResolverService;

    @Override
    public String getCountry(String ip) {
        if (org.springframework.util.StringUtils.isEmpty(ip)){
            log.info("Ip not detected, default country is LV");
            return Constants.DEFAULT_COUNTRY;
        }

        ClientResponse response = countryResolverService.getCountry(ip);

        if (response == null) {
            log.info("Country detection service is unavailable");
            return Constants.DEFAULT_COUNTRY;
        }

        if (response.getStatus() != 200) {
            log.info("Country detection service responded with error: " + response.getStatus());
            return Constants.DEFAULT_COUNTRY;
        }
        FreeGeoIpResult geoIpResult = response.getEntity(FreeGeoIpResult.class);
        String country = geoIpResult.getCountry_code();
        return (country.isEmpty() ? Constants.DEFAULT_COUNTRY : country);
    }


    ConcurrentMap<String, PriorityQueue<Long>> countryRequestMap = new ConcurrentHashMap<>();
    @Override
    public boolean isSpamCompliant(String country) {
        synchronized (this) {
          PriorityQueue<Long> buff = countryRequestMap.getOrDefault(country, new PriorityQueue<>());
            Long now = System.currentTimeMillis();

            buff.add(now);
            while (buff.size() > Constants.NUMBER_OF_SESSIONS_PER_SECOND + 1) {
                buff.poll();
            }
            countryRequestMap.put(country, buff);
            if (buff.size() < Constants.NUMBER_OF_SESSIONS_PER_SECOND + 1) {
                return true;
            } else {
                Long firstItem = buff.peek();
                log.debug(country + ": Millis diff: " + (now-firstItem));
                return (now-firstItem) > 1000;
            }
        }
    }

  @Override
  public void resetState() {
    synchronized (this) {
      countryRequestMap.clear();
    }
  }
}
