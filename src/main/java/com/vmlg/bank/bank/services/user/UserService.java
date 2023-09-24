package com.vmlg.bank.bank.services.user;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.vmlg.bank.bank.domain.user.User;
import com.vmlg.bank.bank.domain.user.UserType;
import com.vmlg.bank.bank.dtos.UserDTO;
import com.vmlg.bank.bank.exceptions.TransactionsException;
import com.vmlg.bank.bank.exceptions.UsersException;
import com.vmlg.bank.bank.repositores.user.UserRepository;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;

    public void validationTransaction(User sender, BigDecimal amount) throws TransactionsException{
        if (sender.getUserType() == UserType.MERCHANT) {
            throw new TransactionsException("Merchant user not allowed to make transactions.");
        }
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new TransactionsException("Insufficient balance.");
        }
    }

    public User findUserById(UUID id) throws UsersException{
        return userRepository.findUserById(id).orElseThrow(() -> new UsersException("User not found"));
    }

    public User createUser(UserDTO data) throws UsersException{
        if (data.balance().compareTo(new BigDecimal(0)) < 0) {
            throw new UsersException("The balance value must be positive.");
        }
        User newUser = new User(data);
        saveUser(newUser);
        return newUser;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void saveUser(User user) throws UsersException{
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UsersException("User already registered", e);
        }
    }
}
