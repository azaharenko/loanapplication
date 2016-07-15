package com.loanapp.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanapp.Application;
import com.loanapp.beans.Client;
import com.loanapp.beans.LoanApplication;
import com.loanapp.repositories.ClientRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.mock;
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
  public static final BigDecimal TEST_AMOUNT = new BigDecimal(13.13);
  
  final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;
  protected LoanApplication mockLoanApplication;

  @Before
  public void setup() {
    instance = new LoanController();
    instance.clientRepository = mock(ClientRepository.class);
    mockLoanApplication = mock(LoanApplication.class);
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

  }

  @Test
  public void createLoanNewUser() throws Exception {
    mockLoanApplication = new LoanApplication();
    mockLoanApplication.setClientId(TEST_CLIENT_ID);
    mockLoanApplication.setFirstName(TEST_FIRST_NAME);
    mockLoanApplication.setLastName(TEST_LAST_NAME);
    mockLoanApplication.setAmount(TEST_AMOUNT);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/loan/{id}", 1)
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.amount").value(TEST_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$.customer.id").value(TEST_CLIENT_ID))
        .andExpect(jsonPath("$.customer.firstName").value(TEST_FIRST_NAME))
        .andExpect(jsonPath("$.customer.lastName").value(TEST_LAST_NAME))
        .andExpect(jsonPath("$.customer.blacklisted").value(false));

    mockMvc.perform(get("/loans/user/{id}", TEST_CLIENT_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].amount").value(TEST_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$[0].customer.id").value(TEST_CLIENT_ID))
        .andExpect(jsonPath("$[0].customer.firstName").value(TEST_FIRST_NAME))
        .andExpect(jsonPath("$[0].customer.lastName").value(TEST_LAST_NAME))
        .andExpect(jsonPath("$[0].customer.blacklisted").value(false));

    mockMvc.perform(get("/loans/all"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].amount").value(TEST_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$[0].customer.id").value(TEST_CLIENT_ID))
        .andExpect(jsonPath("$[0].customer.firstName").value(TEST_FIRST_NAME))
        .andExpect(jsonPath("$[0].customer.lastName").value(TEST_LAST_NAME))
        .andExpect(jsonPath("$[0].customer.blacklisted").value(false));

    //blacklist user
    Client client = new Client(TEST_CLIENT_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
    client.setBlacklisted(true);
    instance.clientRepository.save(client);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isExpectationFailed());
    //expect no changes
    mockMvc.perform(get("/loans/all"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(1)));
    //avoid spam fail
    try {
      TimeUnit.MILLISECONDS.sleep(1001);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    //another user
    client = new Client(TEST_CLIENT_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
    instance.clientRepository.save(client);

    mockLoanApplication = new LoanApplication();
    mockLoanApplication.setClientId(TEST_CLIENT_ID1);
    mockLoanApplication.setFirstName(TEST_FIRST_NAME1);
    mockLoanApplication.setLastName(TEST_LAST_NAME1);
    mockLoanApplication.setAmount(TEST_AMOUNT);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/loan/{id}", 1)
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.amount").value(TEST_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$.customer.id").value(TEST_CLIENT_ID))
        .andExpect(jsonPath("$.customer.firstName").value(TEST_FIRST_NAME))
        .andExpect(jsonPath("$.customer.lastName").value(TEST_LAST_NAME))
        .andExpect(jsonPath("$.customer.blacklisted").value(false));

    mockMvc.perform(get("/loan/{id}", 2)
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.amount").value(TEST_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$.customer.id").value(TEST_CLIENT_ID1))
        .andExpect(jsonPath("$.customer.firstName").value(TEST_FIRST_NAME1))
        .andExpect(jsonPath("$.customer.lastName").value(TEST_LAST_NAME1))
        .andExpect(jsonPath("$.customer.blacklisted").value(false));

    mockMvc.perform(get("/loans/user/{id}", TEST_CLIENT_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].amount").value(TEST_AMOUNT.doubleValue()))
        .andExpect(jsonPath("$[0].customer.id").value(TEST_CLIENT_ID))
        .andExpect(jsonPath("$[0].customer.firstName").value(TEST_FIRST_NAME))
        .andExpect(jsonPath("$[0].customer.lastName").value(TEST_LAST_NAME))
        .andExpect(jsonPath("$[0].customer.blacklisted").value(false));


    mockMvc.perform(get("/loans/all"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(2)));

  }




}
