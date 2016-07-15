package com.loanapp.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CountryResolverServiceImpl.class, RequestServiceImpl.class})
public class CountryResolverServiceTest {

  CountryResolverServiceImpl instance;

  @InjectMocks
  RequestServiceImpl requestService;

  @Before
  public void setUp() {
    instance = new CountryResolverServiceImpl();
    requestService = mock(RequestServiceImpl.class);

  }

  @Test
  public void testGetCountryNotFound() {
    when(instance.getCountry(anyString())).thenReturn(null);
    assertEquals(requestService.getCountry("IP"), "LV");
  }

}
