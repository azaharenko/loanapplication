package com.loanapp.services;

import com.loanapp.utils.Constants;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service("countryResolverService")
public class CountryResolverServiceImpl implements CountryResolverService{
    protected final static Logger log = LogManager.getLogger(CountryResolverServiceImpl.class);
    public ClientResponse getCountry(String ip) {

        Client client = Client.create();
        try {
            WebResource webResource = client.resource(new URI(Constants.COUNTRY_BASE_URL.concat(ip)));
            return webResource.accept("application/json").get(ClientResponse.class);
        } catch (Exception e) {
            log.error("Country resolved service failed with message: " + e.getMessage());
            return null;
        }
    }
}
