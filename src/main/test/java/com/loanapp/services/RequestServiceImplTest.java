package com.loanapp.services;

import com.loanapp.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class RequestServiceImplTest {
  @Value("${com.loanapp.number.session.second}")
  int numberOfSessionsPerSecond;

  RequestServiceImpl instance;

  @InjectMocks
  RequestService requestService;

  @Before
  public void setUp() {
    instance = new RequestServiceImpl();
    requestService = mock(RequestService.class);

  }

  @Test
  public void testSpamCompliance(){
    requestService = new RequestServiceImpl();
    IntStream.range(0, numberOfSessionsPerSecond).parallel().forEach(
        nbr -> {
          assertTrue(requestService.isSpamCompliant("LV"));
        }
    );
    try {
      TimeUnit.MILLISECONDS.sleep(1001);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    assertTrue(requestService.isSpamCompliant("LV"));
  }

  @Test
  public void testSpamComplianceFail(){
    requestService = new RequestServiceImpl();
    IntStream.range(0, numberOfSessionsPerSecond).parallel().forEach(
        nbr -> {
          assertTrue(requestService.isSpamCompliant("LV"));
        }
    );
    assertFalse(requestService.isSpamCompliant("LV"));
  }

  @Test
  public void testSpamComplianceDifferentCountries(){
    requestService = new RequestServiceImpl();
    IntStream.range(0, numberOfSessionsPerSecond).parallel().forEach(
        nbr -> {
          assertTrue(requestService.isSpamCompliant("LV"));
          assertTrue(requestService.isSpamCompliant("EE"));
        }
    );
    assertFalse(requestService.isSpamCompliant("LV"));
    try {
      TimeUnit.MILLISECONDS.sleep(1001);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    assertFalse(requestService.isSpamCompliant("EE"));
    assertFalse(requestService.isSpamCompliant("LV"));

  }

  @Test
  public void testSpamComplianceDifferentCountriesSeparateThreads(){
    requestService = new RequestServiceImpl();
    new Thread(() -> {
      assertTrue(requestService.isSpamCompliant("LV"));
    }).start();
    new Thread(() -> {
      assertTrue(requestService.isSpamCompliant("EE"));
    }).start();
    new Thread(() -> {
      assertTrue(requestService.isSpamCompliant("EE"));
    }).start();
    new Thread(() -> {
      assertTrue(requestService.isSpamCompliant("LV"));
    }).start();

    assertFalse(requestService.isSpamCompliant("LV"));
    try {
      TimeUnit.MILLISECONDS.sleep(1001);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    assertTrue(requestService.isSpamCompliant("EE"));
    assertTrue(requestService.isSpamCompliant("LV"));
  }

}
