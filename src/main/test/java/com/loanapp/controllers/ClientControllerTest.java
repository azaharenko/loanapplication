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

    public static final String ID = "1111";
    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME = "Boskin";

    ClientController instance;

    @Before
    public void setup() {
      instance = new ClientController();
      instance.clientRepository = mock(ClientRepository.class);
    }

    @Test
    public void testFindClientById() {
//      when(instance.clientRepository.findById(ID), (MockHttpServletRequest) anyObject())).thenReturn("url");
//
//      assertEquals("url", instance.start(new MockHttpServletRequest(), "85ee6697-0904-4b50-8072-5661dd61152e"));
//
//      verify(instance.clientRepository).findById(ID)(eq("85ee6697-0904-4b50-8072-5661dd61152e"), (MockHttpServletRequest) anyObject());
      verifyNoMoreInteractions(instance.clientRepository);
    }

}

