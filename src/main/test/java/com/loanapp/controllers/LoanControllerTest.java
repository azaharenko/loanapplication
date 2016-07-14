package com.loanapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanapp.Application;
import com.loanapp.beans.Client;
import com.loanapp.beans.LoanApplication;
import com.loanapp.repositories.ClientRepository;
import com.loanapp.repositories.LoanRepository;
import com.loanapp.services.RequestService;
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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class LoanControllerTest {

  LoanController instance;

  public static final String TEST_COUNTRY = "LV";
  public static final String TEST_ID = "TEST_ID";
  final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  protected LoanApplication mockLoanApplication;


  @Before
  public void setup() {
    instance = new LoanController();
    instance.clientRepository = mock(ClientRepository.class);
    instance.loanRepository = mock(LoanRepository.class);
    instance.requestService = mock(RequestService.class);
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    mockLoanApplication = mock(LoanApplication.class);

  }

  @Test
  public void createLoanWithNewUser() throws Exception {
    mockLoanApplication.setClientId(TEST_ID);
    mockLoanApplication.setFirstName(anyString());
    mockLoanApplication.setLastName(anyString());
//    mockLoanApplication.setAmount(anyInt());
    when(instance.requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(instance.requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(true);
    when(instance.clientRepository.findById(TEST_ID)).thenReturn(null);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());


  }

  @Test
  public void createLoanWithExistingUser() throws Exception {
    Client client = new Client(TEST_ID, anyString(), anyString());
    mockLoanApplication.setClientId(client.getId());
    mockLoanApplication.setFirstName(anyString());
    mockLoanApplication.setLastName(anyString());
//    mockLoanApplication.setAmount(22.33);
    when(instance.requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(instance.requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(true);
    when(instance.clientRepository.findById(client.getId())).thenReturn(client);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

  }

  @Test
  public void createLoanWithExistingUserIncorrectData() throws Exception {
    Client client = new Client(TEST_ID, anyString(), anyString());
    mockLoanApplication.setClientId(client.getId());
    mockLoanApplication.setFirstName(anyString());
    mockLoanApplication.setLastName(anyString());
//    mockLoanApplication.setAmount(anyLong());
    when(instance.requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(instance.requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(true);
    when(instance.clientRepository.findById(client.getId())).thenReturn(client);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict());

  }

  @Test
  public void createLoanSpamCheckFailed() throws Exception {
    mockLoanApplication.setClientId(anyString());
    mockLoanApplication.setFirstName(anyString());
    mockLoanApplication.setLastName(anyString());
//    mockLoanApplication.setAmount(anyLong());
    when(instance.requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(instance.requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isTooManyRequests());

  }

  @Test
  public void createLoanNoClientId() throws Exception {
    mockLoanApplication.setFirstName(anyString());
    mockLoanApplication.setLastName(anyString());
    mockLoanApplication.setAmount(new BigDecimal(1.01));
    when(instance.requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(instance.requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void createLoanNoFirstName() throws Exception {
    mockLoanApplication.setClientId(anyString());
    mockLoanApplication.setLastName(anyString());
    mockLoanApplication.setAmount(new BigDecimal(1.01));
    when(instance.requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(instance.requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void createLoanNoLastName() throws Exception {
    mockLoanApplication.setClientId(anyString());
    mockLoanApplication.setFirstName(anyString());
    mockLoanApplication.setAmount(new BigDecimal(1.01));
    when(instance.requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(instance.requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void createLoanNoAmount() throws Exception {
    mockLoanApplication.setClientId(anyString());
    mockLoanApplication.setFirstName(anyString());
    mockLoanApplication.setLastName(anyString());
    when(instance.requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(instance.requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void createLoanAmountWrongFormat() throws Exception {
    mockLoanApplication.setClientId(anyString());
    mockLoanApplication.setFirstName(anyString());
    mockLoanApplication.setLastName(anyString());
    mockLoanApplication.setAmount(new BigDecimal(1.111));
    when(instance.requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(instance.requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void createLoanAmountBoundariesTooHigh() throws Exception {
    mockLoanApplication.setClientId(anyString());
    mockLoanApplication.setFirstName(anyString());
    mockLoanApplication.setLastName(anyString());
    mockLoanApplication.setAmount(new BigDecimal(1001.00));
    when(instance.requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(instance.requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void createLoanAmountBoundariesTooLow() throws Exception {
    mockLoanApplication.setClientId(anyString());
    mockLoanApplication.setFirstName(anyString());
    mockLoanApplication.setLastName(anyString());
    mockLoanApplication.setAmount(new BigDecimal(0.10));
    when(instance.requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(instance.requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void createLoanAmountNegative() throws Exception {
    mockLoanApplication.setClientId(anyString());
    mockLoanApplication.setFirstName(anyString());
    mockLoanApplication.setLastName(anyString());
    mockLoanApplication.setAmount(new BigDecimal(-3.10));
    when(instance.requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(instance.requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }


}


