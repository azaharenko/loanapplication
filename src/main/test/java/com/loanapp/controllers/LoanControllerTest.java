package com.loanapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanapp.Application;
import com.loanapp.beans.Client;
import com.loanapp.beans.Loan;
import com.loanapp.beans.LoanApplication;
import com.loanapp.repositories.ClientRepository;
import com.loanapp.repositories.LoanRepository;
import com.loanapp.services.RequestService;
import com.loanapp.utils.Constants;
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
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class LoanControllerTest {

  LoanController instance;

  public static final String TEST_COUNTRY = "LV";
  public static final String TEST_CLIENT_ID = "TEST_CLIENT_ID";
  public static final Long TEST_LOAN_ID = 1l;
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
    mockLoanApplication.setClientId(TEST_CLIENT_ID);
    mockLoanApplication.setFirstName(anyString());
    mockLoanApplication.setLastName(anyString());
//    mockLoanApplication.setAmount(anyInt());
    when(instance.requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(instance.requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(true);
    when(instance.clientRepository.findById(TEST_CLIENT_ID)).thenReturn(null);

    mockMvc.perform(post("/loan")
        .content(mapper.writeValueAsString(mockLoanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());


  }

  @Test
  public void createLoanWithExistingUser() throws Exception {
    Client client = new Client(TEST_CLIENT_ID, anyString(), anyString());
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
    Client client = new Client(TEST_CLIENT_ID, anyString(), anyString());
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

  @Test
  public void findLoanById() throws Exception {
    Client client = new Client(TEST_CLIENT_ID, anyString(), anyString());
    Loan loan = new Loan(new BigDecimal(1.10), client);
    loan.setId(TEST_LOAN_ID);
    loan.setLoanCountry(anyString());

    when(instance.loanRepository.findOne(TEST_LOAN_ID)).thenReturn(loan);

    mockMvc.perform(get("/loan/{id}", TEST_LOAN_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.amount").value(loan.getAmount().doubleValue()))
        .andExpect(jsonPath("$.applicationCountry").value(loan.getLoanCountry()))
        .andExpect(jsonPath("$.customer.id").value(loan.getClient().getId()))
        .andExpect(jsonPath("$.customer.firstName").value(loan.getClient().getFirstName()))
        .andExpect(jsonPath("$.customer.lastName").value(loan.getClient().getLastName()))
        .andExpect(jsonPath("$.customer.blacklisted").value(false));

    verify(instance.loanRepository, times(1)).findOne(TEST_LOAN_ID);
    verifyNoMoreInteractions(instance.loanRepository);
  }

  @Test
  public void findLoanByIdNotFound() throws Exception {
    when(instance.loanRepository.findOne(TEST_LOAN_ID)).thenReturn(null);


    mockMvc.perform(get("/loan/{id}", TEST_LOAN_ID))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.type").value("INFO"))
        .andExpect(jsonPath("$.message").value("Loan not found"));

    verify(instance.loanRepository, times(0)).findOne(TEST_LOAN_ID);
    verifyNoMoreInteractions(instance.loanRepository);
  }

  @Test
  public void findLoanByIdIncorrectId() throws Exception {
    when(instance.loanRepository.findOne(TEST_LOAN_ID)).thenReturn(null);

    mockMvc.perform(get("/loan/{id}", anyString()))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.type").value("ERROR"))
        .andExpect(jsonPath("$.message").value(Constants.BAD_REQUEST));

    verify(instance.loanRepository, times(0)).findOne(TEST_LOAN_ID);
    verifyNoMoreInteractions(instance.loanRepository);
  }

  @Test
  public void findAllLoansNotFound() throws Exception {
    when(instance.loanRepository.findAll()).thenReturn(null);

    mockMvc.perform(get("/loans/all"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.type").value("INFO"))
        .andExpect(jsonPath("$.message").value("No approved loan found"));

    verify(instance.loanRepository, times(1)).findAll();
    verifyNoMoreInteractions(instance.loanRepository);

  }

  @Test
  public void findAllLoans() throws Exception {
    Client client = new Client(anyString(), anyString(), anyString());
    Loan loan = new Loan(new BigDecimal(1.10), client);
    Loan loan2 = new Loan(new BigDecimal(1.11), client);
    List<Loan> loans = new ArrayList<Loan>() {{
      add(loan);
      add(loan2);
    }};

    when(instance.loanRepository.findAll()).thenReturn(loans);

    mockMvc.perform(get("/loans/all"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(loans.size())));

    verify(instance.loanRepository, times(1)).findAll();
    verifyNoMoreInteractions(instance.loanRepository);
  }

  @Test
  public void findAllLoansForClientId() throws Exception {
    Client client = new Client(TEST_CLIENT_ID, anyString(), anyString());
    Loan loan = new Loan(new BigDecimal(1.10), client);
    List<Loan> loans = new ArrayList<Loan>() {{
      add(loan);
    }};

    client.setLoans(loans);
    when(instance.clientRepository.findById(TEST_CLIENT_ID)).thenReturn(client);

    mockMvc.perform(get("/loans/client/{id}", TEST_CLIENT_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(loans.size())))
        .andExpect(jsonPath("$[0].client.id").value(loan.getClient().getId()));


    verify(instance.clientRepository, times(1)).findById(TEST_CLIENT_ID);
    verifyNoMoreInteractions(instance.clientRepository);
  }

  @Test
  public void findAllLoansForClientIdLoansNotFound() throws Exception {
    Client client = new Client(TEST_CLIENT_ID, anyString(), anyString());
    client.setLoans(null);
    when(instance.clientRepository.findById(TEST_CLIENT_ID)).thenReturn(client);

    mockMvc.perform(get("/loans/client/{id}", TEST_CLIENT_ID))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.type").value("INFO"))
        .andExpect(jsonPath("$.message").value("Loans not found"));

    verify(instance.clientRepository, times(1)).findById(TEST_CLIENT_ID);
    verifyNoMoreInteractions(instance.clientRepository);

  }

  @Test
  public void findAllLoansForClientIdClientNotFound() throws Exception {
    when(instance.clientRepository.findById(TEST_CLIENT_ID)).thenReturn(null);

    mockMvc.perform(get("/loans/client/{id}", TEST_CLIENT_ID))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.type").value("INFO"))
        .andExpect(jsonPath("$.message").value("Client not found"));

    verify(instance.clientRepository, times(0)).findById(TEST_CLIENT_ID);
    verifyNoMoreInteractions(instance.clientRepository);

  }



}


