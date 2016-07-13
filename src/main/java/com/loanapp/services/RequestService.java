package com.loanapp.services;

import org.springframework.stereotype.Component;

@Component
public interface RequestService {
    String getCountry(String ip);
    boolean isSpamCompliant(String country);
}
