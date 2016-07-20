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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class LoanControllerTest {

  public static final String TEST_COUNTRY = "LV";
  public static final String TEST_CLIENT_ID = "TEST_CLIENT_ID";
  public static final String TEST_FIRST_NAME = "TEST_FIRST_NAME";
  public static final String TEST_LAST_NAME = "TEST_LAST_NAME";
  public static final Long TEST_LOAN_ID = 1l;
  public static final BigDecimal TEST_AMOUNT = new BigDecimal(13.13);

  private MockMvc mockMvc;

  @InjectMocks
  LoanController loanController;

  @Mock
  ClientRepository clientRepository;

  @Mock
  LoanRepository loanRepository;

  @Mock
  RequestService requestService;


  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();
  }

  @Test
  public void testCreateLoanWithNewUser() throws Exception {
    LoanApplication loanApplication = new LoanApplication();
    loanApplication.setClientId(TEST_CLIENT_ID);
    loanApplication.setFirstName(TEST_FIRST_NAME);
    loanApplication.setLastName(TEST_LAST_NAME);
    loanApplication.setAmount(TEST_AMOUNT.setScale(2, BigDecimal.ROUND_HALF_UP));
    when(requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(true);
    when(clientRepository.findById(TEST_CLIENT_ID)).thenReturn(null);

    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());


  }

  @Test
  public void testCreateLoanWithExistingUser() throws Exception {
    Client client = new Client(TEST_CLIENT_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
    LoanApplication loanApplication = new LoanApplication();
    loanApplication.setClientId(TEST_CLIENT_ID);
    loanApplication.setFirstName(TEST_FIRST_NAME);
    loanApplication.setLastName(TEST_LAST_NAME);
    loanApplication.setAmount(TEST_AMOUNT.setScale(2, BigDecimal.ROUND_HALF_UP));
    when(requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(true);
    when(clientRepository.findById(TEST_CLIENT_ID)).thenReturn(client);

    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

  }

  @Test
  public void testCreateLoanSpamCheckFailed() throws Exception {
    LoanApplication loanApplication = new LoanApplication();
    loanApplication.setClientId(TEST_CLIENT_ID);
    loanApplication.setFirstName(TEST_FIRST_NAME);
    loanApplication.setLastName(TEST_LAST_NAME);
    loanApplication.setAmount(TEST_AMOUNT.setScale(2, BigDecimal.ROUND_HALF_UP));
    when(requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isTooManyRequests());

  }

  @Test
  public void testCreateLoanNoClientId() throws Exception {
    LoanApplication loanApplication = new LoanApplication();
    loanApplication.setFirstName(TEST_FIRST_NAME);
    loanApplication.setLastName(TEST_LAST_NAME);
    loanApplication.setAmount(TEST_AMOUNT.setScale(2, BigDecimal.ROUND_HALF_UP));
    when(requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateLoanNoFirstName() throws Exception {
    LoanApplication loanApplication = new LoanApplication();
    loanApplication.setClientId(TEST_CLIENT_ID);
    loanApplication.setLastName(TEST_LAST_NAME);
    loanApplication.setAmount(TEST_AMOUNT.setScale(2, BigDecimal.ROUND_HALF_UP));
    when(requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void testCreateLoanNoLastName() throws Exception {
    LoanApplication loanApplication = new LoanApplication();
    loanApplication.setClientId(TEST_CLIENT_ID);
    loanApplication.setFirstName(TEST_FIRST_NAME);
    loanApplication.setAmount(TEST_AMOUNT.setScale(2, BigDecimal.ROUND_HALF_UP));
    when(requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void testCreateLoanNoAmount() throws Exception {
    LoanApplication loanApplication = new LoanApplication();
    loanApplication.setClientId(TEST_CLIENT_ID);
    loanApplication.setFirstName(TEST_FIRST_NAME);
    loanApplication.setLastName(TEST_LAST_NAME);
    when(requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void testCreateLoanAmountWrongFormat() throws Exception {
    LoanApplication loanApplication = new LoanApplication();
    loanApplication.setClientId(TEST_CLIENT_ID);
    loanApplication.setFirstName(TEST_FIRST_NAME);
    loanApplication.setLastName(TEST_LAST_NAME);
    loanApplication.setAmount(new BigDecimal(22.222).setScale(3, BigDecimal.ROUND_HALF_UP));
    when(requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void testCreateLoanAmountBoundariesTooHigh() throws Exception {
    LoanApplication loanApplication = new LoanApplication();
    loanApplication.setClientId(TEST_CLIENT_ID);
    loanApplication.setFirstName(TEST_FIRST_NAME);
    loanApplication.setLastName(TEST_LAST_NAME);
    loanApplication.setAmount(new BigDecimal(2002.10).setScale(2, BigDecimal.ROUND_HALF_UP));
    when(requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void testCreateLoanAmountBoundariesTooLow() throws Exception {
    LoanApplication loanApplication = new LoanApplication();
    loanApplication.setClientId(TEST_CLIENT_ID);
    loanApplication.setFirstName(TEST_FIRST_NAME);
    loanApplication.setLastName(TEST_LAST_NAME);
    loanApplication.setAmount(new BigDecimal(0.10).setScale(2, BigDecimal.ROUND_HALF_UP));
    when(requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void testCreateLoanAmountNegative() throws Exception {
    LoanApplication loanApplication = new LoanApplication();
    loanApplication.setClientId(TEST_CLIENT_ID);
    loanApplication.setFirstName(TEST_FIRST_NAME);
    loanApplication.setLastName(TEST_LAST_NAME);
    loanApplication.setAmount(new BigDecimal(-10.10).setScale(2, BigDecimal.ROUND_HALF_UP));
    when(requestService.getCountry(anyString())).thenReturn(TEST_COUNTRY);
    when(requestService.isSpamCompliant(TEST_COUNTRY)).thenReturn(false);

    mockMvc.perform(post("/loan")
        .content(objectToJsonString(loanApplication))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  public void testFindLoanById() throws Exception {
    Client client = new Client(TEST_CLIENT_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
    Loan loan = new Loan(new BigDecimal(1.10).setScale(2, BigDecimal.ROUND_HALF_UP), client);
    loan.setId(TEST_LOAN_ID);
    loan.setLoanCountry(TEST_COUNTRY);

    when(loanRepository.findOne(TEST_LOAN_ID)).thenReturn(loan);

    mockMvc.perform(get("/loan/{id}", TEST_LOAN_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.amount").value(loan.getAmount().doubleValue()))
        .andExpect(jsonPath("$.loanCountry").value(loan.getLoanCountry()))
        .andExpect(jsonPath("$.client.id").value(loan.getClient().getId()))
        .andExpect(jsonPath("$.client.firstName").value(loan.getClient().getFirstName()))
        .andExpect(jsonPath("$.client.lastName").value(loan.getClient().getLastName()))
        .andExpect(jsonPath("$.client.blacklisted").value(false));

    verify(loanRepository, times(1)).findOne(TEST_LOAN_ID);
    verifyNoMoreInteractions(loanRepository);
  }

  @Test
  public void testFindLoanByIdNotFound() throws Exception {
    when(loanRepository.findOne(TEST_LOAN_ID)).thenReturn(null);


    mockMvc.perform(get("/loan/{id}", TEST_LOAN_ID))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.type").value("INFO"))
        .andExpect(jsonPath("$.message").value("Loan not found"));

    verify(loanRepository, times(1)).findOne(TEST_LOAN_ID);
    verifyNoMoreInteractions(loanRepository);
  }

  @Test
  public void testFindLoanByIdIncorrectId() throws Exception {
    when(loanRepository.findOne(TEST_LOAN_ID)).thenReturn(null);

    mockMvc.perform(get("/loan/{id}", "wrongLoanId"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.type").value("ERROR"))
        .andExpect(jsonPath("$.message").value(Constants.BAD_REQUEST));

    verify(loanRepository, times(0)).findOne(TEST_LOAN_ID);
    verifyNoMoreInteractions(loanRepository);
  }

  @Test
  public void testFindAllLoansNotFound() throws Exception {
    when(loanRepository.findAll()).thenReturn(null);

    mockMvc.perform(get("/loans/all"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.type").value("INFO"))
        .andExpect(jsonPath("$.message").value("No approved loan found"));

    verify(loanRepository, times(1)).findAll();
    verifyNoMoreInteractions(loanRepository);

  }

  @Test
  public void testFindAllLoans() throws Exception {
    Client client = new Client(TEST_CLIENT_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
    Loan loan = new Loan(new BigDecimal(1.10), client);
    Loan loan2 = new Loan(new BigDecimal(1.11), client);
    List<Loan> loans = new ArrayList<Loan>() {{
      add(loan);
      add(loan2);
    }};

    when(loanRepository.findAll()).thenReturn(loans);

    mockMvc.perform(get("/loans/all"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(loans.size())));

    verify(loanRepository, times(1)).findAll();
    verifyNoMoreInteractions(loanRepository);
  }

  @Test
  public void testFindAllLoansForClientId() throws Exception {
    Client client = new Client(TEST_CLIENT_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
    Loan loan = new Loan(new BigDecimal(1.10), client);
    List<Loan> loans = new ArrayList<Loan>() {{
      add(loan);
    }};

    client.setLoans(loans);
    when(clientRepository.findById(TEST_CLIENT_ID)).thenReturn(client);

    mockMvc.perform(get("/loans/client/{id}", TEST_CLIENT_ID))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$", hasSize(loans.size())))
        .andExpect(jsonPath("$[0].client.id").value(loan.getClient().getId()));

    verify(clientRepository, times(1)).findById(TEST_CLIENT_ID);
    verifyNoMoreInteractions(clientRepository);
  }

  @Test
  public void testFindAllLoansForClientIdLoansNotFound() throws Exception {
    Client client = new Client(TEST_CLIENT_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
    client.setLoans(null);
    when(clientRepository.findById(TEST_CLIENT_ID)).thenReturn(client);

    mockMvc.perform(get("/loans/client/{id}", TEST_CLIENT_ID))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.type").value("INFO"))
        .andExpect(jsonPath("$.message").value("Loans not found"));

    verify(clientRepository, times(1)).findById(TEST_CLIENT_ID);
    verifyNoMoreInteractions(clientRepository);
  }

  @Test
  public void testFindAllLoansForClientIdClientNotFound() throws Exception {
    when(clientRepository.findById(TEST_CLIENT_ID)).thenReturn(null);

    mockMvc.perform(get("/loans/client/{id}", TEST_CLIENT_ID))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.type").value("INFO"))
        .andExpect(jsonPath("$.message").value("Client not found"));

    verify(clientRepository, times(1)).findById(TEST_CLIENT_ID);
    verifyNoMoreInteractions(clientRepository);
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


