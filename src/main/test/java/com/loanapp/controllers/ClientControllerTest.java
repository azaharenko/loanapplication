package com.loanapp.controllers;

import com.loanapp.Application;
import com.loanapp.beans.Client;
import com.loanapp.repositories.ClientRepository;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class ClientControllerTest {

    public static final String TEST_ID = "1111";
    public static final String TEST_FIRST_NAME = "TEST_FIRST_NAME";
    public static final String TEST_LAST_NAME = "TEST_LAST_NAME";

  private MockMvc mockMvc;

  @InjectMocks
  ClientController clientController;

  @Mock
  ClientRepository clientRepository;
    @Before
    public void setup() {
      MockitoAnnotations.initMocks(this);
      this.mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
    }

    @Test
    public void testFindClientById() throws Exception {
      Client client = new Client(TEST_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
      when(clientRepository.findById(TEST_ID)).thenReturn(client);

      mockMvc.perform(get("/client/{id}", TEST_ID))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(jsonPath("$.id").value(TEST_ID))
          .andExpect(jsonPath("$.firstName").value(TEST_FIRST_NAME))
          .andExpect(jsonPath("$.lastName").value(TEST_LAST_NAME));


      verify(clientRepository, times(1)).findById(TEST_ID);
      verifyNoMoreInteractions(clientRepository);
    }

    @Test
    public void testFindClientById1() throws Exception {
      when(clientRepository.findById(TEST_ID)).thenReturn(null);

      mockMvc.perform(get("/client/{id}", TEST_ID))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(jsonPath("$.type").value("INFO"))
          .andExpect(jsonPath("$.message").value("Client not found"));

      verify(clientRepository, times(1)).findById(TEST_ID);
      verifyNoMoreInteractions(clientRepository);
    }

    @Test
    public void testBlacklistClient() throws Exception {
      Client client = new Client(TEST_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
      when(clientRepository.findById(TEST_ID)).thenReturn(client);
      mockMvc.perform(put("/client/blacklist/add/{id}", TEST_ID))
          .andExpect(status().isOk());
    }

    @Test
    public void testBlacklistClient1() throws Exception {
      when(clientRepository.findById(TEST_ID)).thenReturn(null);
      mockMvc.perform(put("/client/blacklist/add/{id}", TEST_ID))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(jsonPath("$.type").value("INFO"))
          .andExpect(jsonPath("$.message").value("Client not found"));
    }

    @Test
    public void testBlacklistClient2() throws Exception {
      Client client = new Client(TEST_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
      client.setBlacklisted(true);
      when(clientRepository.findById(TEST_ID)).thenReturn(client);

      mockMvc.perform(put("/client/blacklist/add/{id}", TEST_ID))
          .andExpect(status().isAlreadyReported())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(jsonPath("$.type").value("INFO"))
          .andExpect(jsonPath("$.message").value("Client already blacklisted"));

    }

    @Test
    public void testRemoveFromBlacklistClient() throws Exception {
      Client client = new Client(TEST_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
      client.setBlacklisted(true);
      when(clientRepository.findById(client.getId())).thenReturn(client);

      mockMvc.perform(put("/client/blacklist/remove/{id}", client.getId()))
          .andExpect(status().isOk());

    }

    @Test
    public void testRemoveFromBlacklistClient1() throws Exception {
      when(clientRepository.findById(TEST_ID)).thenReturn(null);

      mockMvc.perform(put("/client/blacklist/remove/{id}", TEST_ID))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(jsonPath("$.type").value("INFO"))
          .andExpect(jsonPath("$.message").value("Client not found"));
    }

    @Test
    public void testBlacklistClient3() throws Exception {
      Client client = new Client(TEST_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
      client.setBlacklisted(false);
      when(clientRepository.findById(TEST_ID)).thenReturn(client);

      mockMvc.perform(put("/client/blacklist/remove/{id}", TEST_ID))
          .andExpect(status().isAlreadyReported())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(jsonPath("$.type").value("INFO"))
          .andExpect(jsonPath("$.message").value("Client not in blacklist"));
    }

}

