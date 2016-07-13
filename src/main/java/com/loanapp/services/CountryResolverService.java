package com.loanapp.services;

import com.sun.jersey.api.client.ClientResponse;
import org.springframework.stereotype.Component;

@Component
public interface CountryResolverService {
    ClientResponse getCountry(String ip);
}
