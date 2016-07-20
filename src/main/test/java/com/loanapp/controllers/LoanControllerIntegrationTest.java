package com.loanapp.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanapp.Application;
import com.loanapp.beans.Client;
import com.loanapp.beans.LoanApplication;
import com.loanapp.repositories.ClientRepository;
import com.loanapp.services.RequestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class LoanControllerIntegrationTest {

  LoanController instance;

  public static final String TEST_CLIENT_ID = "TEST_CLIENT_ID";
  public static final String TEST_FIRST_NAME = "TEST_FIRST_NAME";
  public static final String TEST_LAST_NAME = "TEST_LAST_NAME";
  public static final String TEST_CLIENT_ID1 = "TEST_CLIENT_ID";
  public static final String TEST_FIRST_NAME1 = "TEST_FIRST_NAME";
  public static final String TEST_LAST_NAME1 = "TEST_LAST_NAME";
  public static final BigDecimal TEST_AMOUNT = new BigDecimal(13.14);

  private MockMvc mockMvc;
  @Autowired
  LoanController loanController;

  @Autowired
  ClientRepository clientRepository;

  @InjectMocks
  RequestService requestService;

  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();
  }

  @Test
  public void createLoanNewUser() throws Exception {
    LoanApplication loanApplication = new LoanApplication();
    loanApplication.setClientId(TEST_CLIENT_ID);
    loanApplication.setFirstName(TEST_FIRST_NAME);
    loanApplication.setLastName(TEST_LAST_NAME);
    loanApplication.setAmount(TEST_AMOUNT.setScale(2, BigDecimal.ROUND_HALF_UP));

    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/loan/{id}", 1)
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.amount").value(TEST_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$.client.id").value(TEST_CLIENT_ID))
        .andExpect(jsonPath("$.client.firstName").value(TEST_FIRST_NAME))
        .andExpect(jsonPath("$.client.lastName").value(TEST_LAST_NAME))
        .andExpect(jsonPath("$.client.blacklisted").value(false));

    mockMvc.perform(get("/loans/client/{id}", TEST_CLIENT_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].amount").value(TEST_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$[0].client.id").value(TEST_CLIENT_ID))
        .andExpect(jsonPath("$[0].client.firstName").value(TEST_FIRST_NAME))
        .andExpect(jsonPath("$[0].client.lastName").value(TEST_LAST_NAME))
        .andExpect(jsonPath("$[0].client.blacklisted").value(false));

    mockMvc.perform(get("/loans/all"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].amount").value(TEST_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$[0].client.id").value(TEST_CLIENT_ID))
        .andExpect(jsonPath("$[0].client.firstName").value(TEST_FIRST_NAME))
        .andExpect(jsonPath("$[0].client.lastName").value(TEST_LAST_NAME))
        .andExpect(jsonPath("$[0].client.blacklisted").value(false));

    //blacklist user
    Client client = new Client(TEST_CLIENT_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
    client.setBlacklisted(true);
    clientRepository.save(client);

    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isExpectationFailed());
    //expect no changes
    mockMvc.perform(get("/loans/all"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(1)));
    try {
      TimeUnit.MILLISECONDS.sleep(1001);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    client = new Client(TEST_CLIENT_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
    clientRepository.save(client);

    loanApplication = new LoanApplication();
    loanApplication.setClientId(TEST_CLIENT_ID1);
    loanApplication.setFirstName(TEST_FIRST_NAME1);
    loanApplication.setLastName(TEST_LAST_NAME1);
    loanApplication.setAmount(TEST_AMOUNT.setScale(2, BigDecimal.ROUND_HALF_UP));
    TimeUnit.MILLISECONDS.sleep(2001);
    Thread.sleep(2001);


    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/loan/{id}", 1)
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.amount").value(TEST_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$.client.id").value(TEST_CLIENT_ID))
        .andExpect(jsonPath("$.client.firstName").value(TEST_FIRST_NAME))
        .andExpect(jsonPath("$.client.lastName").value(TEST_LAST_NAME))
        .andExpect(jsonPath("$.client.blacklisted").value(false));

    mockMvc.perform(get("/loan/{id}", 2)
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.amount").value(TEST_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$.client.id").value(TEST_CLIENT_ID1))
        .andExpect(jsonPath("$.client.firstName").value(TEST_FIRST_NAME1))
        .andExpect(jsonPath("$.client.lastName").value(TEST_LAST_NAME1))
        .andExpect(jsonPath("$.client.blacklisted").value(false));

    mockMvc.perform(get("/loans/client/{id}", TEST_CLIENT_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].amount").value(TEST_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$[0].client.id").value(TEST_CLIENT_ID))
        .andExpect(jsonPath("$[0].client.firstName").value(TEST_FIRST_NAME))
        .andExpect(jsonPath("$[0].client.lastName").value(TEST_LAST_NAME))
        .andExpect(jsonPath("$[0].client.blacklisted").value(false));


    mockMvc.perform(get("/loans/all"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(2)));

  }

  public static String objectToJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }



}
