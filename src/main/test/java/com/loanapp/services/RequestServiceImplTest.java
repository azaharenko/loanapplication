package com.loanapp.services;

import com.loanapp.Application;
import com.loanapp.utils.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  RequestService requestService;

  @Before
  public void setUp() {
     requestService.resetState();
  }

  @Test
  public void testSpamCompliance(){
    IntStream.range(0, Constants.NUMBER_OF_SESSIONS_PER_SECOND).parallel().forEach(
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
    IntStream.range(0, Constants.NUMBER_OF_SESSIONS_PER_SECOND).parallel().forEach(
        nbr -> {
          assertTrue(requestService.isSpamCompliant("LV"));
        }
    );
    assertFalse(requestService.isSpamCompliant("LV"));
  }

  @Test
  public void testSpamComplianceDifferentCountriesOneThread(){
    IntStream.range(0, Constants.NUMBER_OF_SESSIONS_PER_SECOND).parallel().forEach(
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

    assertTrue(requestService.isSpamCompliant("LV"));
    assertTrue(requestService.isSpamCompliant("EE"));

  }

  @Test
  public void testSpamComplianceDifferentCountriesSeparateThreads(){
    IntStream.range(0, Constants.NUMBER_OF_SESSIONS_PER_SECOND).parallel().forEach(
        nbr -> {
          assertTrue(requestService.isSpamCompliant("LV"));
        }
    );
    IntStream.range(0, Constants.NUMBER_OF_SESSIONS_PER_SECOND).parallel().forEach(
        nbr -> {
          assertTrue(requestService.isSpamCompliant("EE"));
        }
    );
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
