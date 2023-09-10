package com.vmlg.bank.bank.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmlg.bank.bank.domain.transaction.Transaction;
import com.vmlg.bank.bank.domain.user.User;
import com.vmlg.bank.bank.dtos.AuthenticationDTO;
import com.vmlg.bank.bank.dtos.RegisterDTO;
import com.vmlg.bank.bank.dtos.TransactionDTO;
import com.vmlg.bank.bank.dtos.UserDTO;
import com.vmlg.bank.bank.dtos.LoginResponseDTO;
import com.vmlg.bank.bank.services.TokenService;
import com.vmlg.bank.bank.services.TransactionService;
import com.vmlg.bank.bank.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

     @Autowired
    private TransactionService transactionService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(
            data.email(), data.password()
        );
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody @Valid UserDTO user) throws Exception{
        String encryptedPasswd = new BCryptPasswordEncoder().encode(user.password());
        UserDTO userDto = new UserDTO(
            user.firstName(),
            user.lastName(),
            user.document(),
            user.balance(),
            user.email(),
            encryptedPasswd,
            user.userType()
            );
        User newUser = userService.createUser(userDto);
        return new ResponseEntity<User>(newUser, HttpStatus.CREATED);
        // return ResponseEntity.ok().build();
    }
}
