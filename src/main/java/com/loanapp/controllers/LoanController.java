package com.loanapp.controllers;

import com.loanapp.beans.Client;
import com.loanapp.beans.Loan;
import com.loanapp.beans.LoanApplication;
import com.loanapp.beans.Message;
import com.loanapp.repositories.ClientRepository;
import com.loanapp.repositories.LoanRepository;
import com.loanapp.services.RequestService;
import com.loanapp.utils.Constants;
import com.loanapp.utils.MessageType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@PropertySource("file:${user.home}/.loanapplication/config.properties")
public class LoanController {
    protected final static Logger log = LogManager.getLogger(LoanController.class);

    @Autowired
    RequestService requestService;
    @Autowired
    LoanRepository loanRepository;
    @Autowired
    ClientRepository clientRepository;

    @Value("${com.loanapp.default.country}")
    String defaultCountry;

    @RequestMapping(value = "/loan", method = RequestMethod.POST)
    public ResponseEntity<?> createLoan(@Valid @RequestBody LoanApplication loanApplication, UriComponentsBuilder ucBuilder, HttpServletRequest request) {
      log.info("Legacy REST endpoint called: POST /loan/ from IP = " + request.getRemoteAddr());

      String country = ((request.getRemoteAddr() != null) ? requestService.getCountry(request.getRemoteAddr()) : defaultCountry);
      if (requestService.isSpamCompliant(country)) {

        String clientId = loanApplication.getClientId();
        Client client = clientRepository.findById(clientId);
        if (client != null) {
          if (!isUserDataCorrect(client, loanApplication)) {
            log.info("Incorrect data provided for client id=" + clientId);
            return new ResponseEntity<>(new Message(MessageType.ERROR, "Incorrect data provided for"), HttpStatus.CONFLICT);
          }

          if (client.isBlacklisted()) {
            log.info(String.format("Client with id=%s is blacklisted", clientId));
            return new ResponseEntity<>(new Message(MessageType.ERROR, "Client is blacklisted"), HttpStatus.EXPECTATION_FAILED);
          }

        } else {
          client = new Client(clientId, loanApplication.getFirstName(), loanApplication.getLastName());
        }
        clientRepository.save(client);
        log.info("Client created " + client.getId());

        Loan loan = new Loan(loanApplication.getAmount(), client);
        loan.setLoanCountry(country);
        loanRepository.save(loan);
        log.info("Loan created " + loan.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/loan/{id}").buildAndExpand(loan.getId()).toUri());
        return new ResponseEntity<Void>(null, headers, HttpStatus.CREATED);
      } else {
        log.info("Rejecting, Too many requests from " + country);
        return new ResponseEntity<>(new Message(MessageType.ERROR, "Rejected: Too many requests from " + country), HttpStatus.TOO_MANY_REQUESTS);
      }
    }

    @RequestMapping(value = "/loan/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findLoanById(@PathVariable("id") long id) {
        log.info("Finding Loan application " + id);
        Loan loan = loanRepository.findOne(id);

        if (loan == null){
            log.info(String.format("Loan with id-%s cannot be found", id));
            return new ResponseEntity<>(new Message(MessageType.INFO, "Loan not found"), HttpStatus.NOT_FOUND);
        }
        log.info(String.format("Found Loan Application %s %s %s", loan.getId(), loan.getClient().getId(), loan.getAmount().toString()));
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }

    @RequestMapping(value = "/loans/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllLoans() {
        log.info("Legacy REST endpoint called: POST /loans/all/");
        Iterable<Loan> loans = loanRepository.findAll();

        if (loans == null){
            log.info("No approved loan found");
            return new ResponseEntity<>(new Message(MessageType.INFO, "No approved loan found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @RequestMapping(value = "/loans/client/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllLoansForClient(@PathVariable("id") String id) {
        log.info("Finding All Approved Loan  For Client " + id);
        Client client = clientRepository.findById(id);

        if (client == null){
            log.info(String.format("Client with id %s not found", id));
            return new ResponseEntity<>(new Message(MessageType.INFO, "Client not found"), HttpStatus.NOT_FOUND);
        }

        if (CollectionUtils.isEmpty(client.getLoans())){
            log.info(String.format("Loans for client with id %s not found", id));
            return new ResponseEntity<>(new Message(MessageType.INFO, "Loans not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(client.getLoans(), HttpStatus.OK);
    }

    private boolean isUserDataCorrect(Client client, LoanApplication application) {
        return client.getFirstName().equals(application.getFirstName()) && client.getLastName().equals(application.getLastName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(new Message(MessageType.ERROR, Constants.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<?> handleNumberFormatException(NumberFormatException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(new Message(MessageType.ERROR, Constants.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error(ex.getMessage());
        return new ResponseEntity<>(new Message(MessageType.ERROR, Constants.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

}
