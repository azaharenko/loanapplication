package com.loanapp.controllers;

import com.loanapp.beans.Client;
import com.loanapp.beans.Message;
import com.loanapp.repositories.ClientRepository;
import com.loanapp.utils.MessageType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class ClientController {
    protected final static Logger log = LogManager.getLogger(ClientController.class);


    @Autowired
    ClientRepository clientRepository;

    @RequestMapping(value = "/client/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findClientById(@PathVariable("id") String id) {
        log.info("Finding client " + id);
        Client client = clientRepository.findById(id);

        if (client == null){
            log.info(String.format("Client with id %s not found", id));
            return new ResponseEntity<>(new Message(MessageType.INFO, "Client not found"), HttpStatus.NOT_FOUND);
        }
        log.info(String.format("Found Client %s %s %s", id, client.getFirstName(), client.getLastName()));
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    @RequestMapping(value = "/client/blacklist/add/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> blacklistClient(@PathVariable("id") String id) {
        log.info("Blacklisting client " + id);
        Client client = clientRepository.findById(id);
        if (client == null){
            log.info(String.format("Client with id %s not found", id));
            return new ResponseEntity<>(new Message(MessageType.INFO, "Client not found"), HttpStatus.NOT_FOUND);
        }

        if (client.isBlacklisted()){
            log.info(String.format("Client with id %s already blacklisted", id));
            return new ResponseEntity<>(new Message(MessageType.INFO, "Client already blacklisted"), HttpStatus.ALREADY_REPORTED);
        }

        client.setBlacklisted(true);

        clientRepository.save(client);

        log.info(String.format("Client with id %s was modified", id));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(value = "/client/blacklist/remove/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeFromBlacklistClient(@PathVariable("id") String id) {
      log.info("Remove from blacklisting client " + id);
      Client client = clientRepository.findById(id);
      if (client == null){
        log.info(String.format("Client with id %s not found", id));
        return new ResponseEntity<>(new Message(MessageType.INFO, "Client not found"), HttpStatus.NOT_FOUND);
      }

      if (client.isBlacklisted()){
        log.info(String.format("Client with id %s removed from blacklist", id));
        client.setBlacklisted(false);
      } else {
        log.info(String.format("Client with id %s not in blacklist", id));
        return new ResponseEntity<>(new Message(MessageType.INFO, "Client not in blacklist"), HttpStatus.ALREADY_REPORTED);
      }

      clientRepository.save(client);

      log.info(String.format("Client with id %s was removed from blacklist", id));
      return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
