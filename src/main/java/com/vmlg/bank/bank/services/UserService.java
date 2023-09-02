package com.vmlg.bank.bank.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vmlg.bank.bank.domain.user.User;
import com.vmlg.bank.bank.domain.user.UserType;
import com.vmlg.bank.bank.dtos.UserDTO;
import com.vmlg.bank.bank.repositores.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void validationTransaction(User sender, BigDecimal amount) throws Exception{
        if (sender.getUserType() == UserType.MERCHANT) {
            throw new Exception("Merchant user not allowed to make transactions.");
        }
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new Exception("Insufficient balance.");
        }
    }

    public User findUserById(UUID id) throws Exception{
        return userRepository.findUserById(id).orElseThrow(() -> new Exception("User not found"));
    }

    public User createUser(UserDTO data){
        User newUser = new User(data);
        saveUser(newUser);
        return newUser;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public void saveUser(User user){
        userRepository.save(user);
    }
}
