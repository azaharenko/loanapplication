package com.loanapp.controllers;

import com.loanapp.Application;
import com.loanapp.beans.Client;
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

    ClientController instance;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setup() {
      instance = new ClientController();
      instance.clientRepository = mock(ClientRepository.class);
      mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testFindClientById() throws Exception {
      Client client = new Client(TEST_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
      when(instance.clientRepository.findById(TEST_ID)).thenReturn(client);

      mockMvc.perform(get("/client/{id}", TEST_ID))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(jsonPath("$.id").value(TEST_ID))
          .andExpect(jsonPath("$.firstName").value(anyString()))
          .andExpect(jsonPath("$.lastName").value(anyString()));


      verify(instance.clientRepository, times(1)).findById(TEST_ID);
      verifyNoMoreInteractions(instance.clientRepository);
    }

    @Test
    public void testFindClientById1() throws Exception {
      when(instance.clientRepository.findById(anyString())).thenReturn(null);

      mockMvc.perform(get("/client/{id}", anyString()))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(jsonPath("$.type").value("INFO"))
          .andExpect(jsonPath("$.message").value("Client not found"));

      verify(instance.clientRepository, times(1)).findById(anyString());
      verifyNoMoreInteractions(instance.clientRepository);
    }

    @Test
    public void testBlacklistClient() throws Exception {
      Client client = new Client(TEST_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
      when(instance.clientRepository.findById(client.getId())).thenReturn(client);
      mockMvc.perform(put("/customer/blacklist/add/{id}", client.getId()))
          .andExpect(status().isOk());

      verify(instance.clientRepository.findById(client.getId()).isBlacklisted());
    }

    @Test
    public void testBlacklistClient1() throws Exception {
      when(instance.clientRepository.findById(anyString())).thenReturn(null);
      mockMvc.perform(put("/customer/blacklist/add/{id}", anyString()))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(jsonPath("$.type").value("INFO"))
          .andExpect(jsonPath("$.message").value("Client not found"));
    }

    @Test
    public void testBlacklistClient2() throws Exception {
      Client client = new Client(TEST_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
      client.setBlacklisted(true);
      when(instance.clientRepository.findById(client.getId())).thenReturn(client);

      mockMvc.perform(put("/customer/blacklist/add/{id}", client.getId()))
          .andExpect(status().isAlreadyReported())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(jsonPath("$.type").value("INFO"))
          .andExpect(jsonPath("$.message").value("Client already blacklisted"));

      verify(instance.clientRepository.findById(client.getId()).isBlacklisted());
    }

    @Test
    public void testRemoveFromBlacklistClient() throws Exception {
      Client client = new Client(TEST_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
      client.setBlacklisted(true);
      when(instance.clientRepository.findById(client.getId())).thenReturn(client);

      mockMvc.perform(put("/customer/blacklist/remove/{id}", client.getId()))
          .andExpect(status().isOk());

      verify(!instance.clientRepository.findById(client.getId()).isBlacklisted());
    }

    @Test
    public void testRemoveFromBlacklistClient1() throws Exception {
      when(instance.clientRepository.findById(anyString())).thenReturn(null);

      mockMvc.perform(put("/customer/blacklist/remove/{id}", anyString()))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(jsonPath("$.type").value("INFO"))
          .andExpect(jsonPath("$.message").value("Client not found"));
    }

    @Test
    public void testBlacklistClient3() throws Exception {
      Client client = new Client(TEST_ID, TEST_FIRST_NAME, TEST_LAST_NAME);
      client.setBlacklisted(false);
      when(instance.clientRepository.findById(client.getId())).thenReturn(client);

      mockMvc.perform(put("/customer/blacklist/remove/{id}", client.getId()))
          .andExpect(status().isAlreadyReported())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
          .andExpect(jsonPath("$.type").value("INFO"))
          .andExpect(jsonPath("$.message").value("Client not in blacklist"));

      verify(instance.clientRepository.findById(client.getId()).isBlacklisted());
    }

}

