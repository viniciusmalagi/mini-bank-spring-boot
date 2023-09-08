package com.vmlg.bank.bank.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vmlg.bank.bank.domain.user.User;
import com.vmlg.bank.bank.dtos.AuthenticationDTO;
import com.vmlg.bank.bank.dtos.UserDTO;
import com.vmlg.bank.bank.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
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
        userService.createUser(userDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }
}
