package com.vmlg.bank.bank.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vmlg.bank.bank.domain.user.User;
import com.vmlg.bank.bank.dtos.AuthenticationDTO;
import com.vmlg.bank.bank.dtos.UserDTO;
import com.vmlg.bank.bank.exceptions.UsersException;
import com.vmlg.bank.bank.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("{id}")
    public ResponseEntity<User> getCurrentUser(@PathVariable("id") UUID uuid) throws UsersException{
        return new ResponseEntity<User>(userService.findUserById(uuid), HttpStatus.OK);
    }
    @GetMapping("{id}/balance")
    public ResponseEntity<BigDecimal> getUserBalance(@PathVariable("id") UUID uuid) throws UsersException{
        return new ResponseEntity<BigDecimal>(userService.findUserById(uuid).getBalance(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }
}
