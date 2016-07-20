package com.loanapp.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CountryResolverServiceImpl.class, RequestServiceImpl.class})

public class CountryResolverServiceTest {

  @Mock
  CountryResolverServiceImpl countryResolverService;

  @Autowired
  RequestServiceImpl requestServiceImpl;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testGetCountry_Null_Response() {
    when(countryResolverService.getCountry(anyString())).thenReturn(null);
    assertEquals(requestServiceImpl.getCountry("IP"), "LV");
  }

}
