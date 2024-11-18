package com.microservicesapp.passwordgeneration.Service;

import com.microservicesapp.passwordgeneration.Exception.UserAlreadyExistsException;
import com.microservicesapp.passwordgeneration.Repository.UserRepository;
import com.microservicesapp.passwordgeneration.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public String registerUser(User user) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
        user.setExiryDate(LocalDateTime.from(LocalDateTime.now().plusMonths(3)));
        user.setCreateAt(new Date());
        userRepository.save(user);
        return "User created successfully";
    }
    public String LoginUser(User user) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isPresent()) {
            String password = userOptional.get().getPassword();
            if (user.getPassword().equals(password)) {
                User user1 = userRepository.findByUsername(user.getUsername()).get();
                user1.setLoginAt(LocalDateTime.now());
                userRepository.save(user1);
                return "User logged in successfully";
            }else {
                throw new RuntimeException("Username or password incorrect");
            }
        }
        return "User logged in successfully";
    }


    public String LogoutUser(User user) {
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            // Check if the user's session has expired
            LocalDateTime now = LocalDateTime.now();
            if (existingUser.getLoginAt() != null && existingUser.getLoginAt().plusMinutes(2).isBefore(now)) { // Example: 1 hour session
                throw new RuntimeException("Session has expired. Please log in again.");
            }

            existingUser.setLogoutAt(LocalDateTime.now());
            userRepository.save(existingUser);
            return existingUser.getUsername();
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }else {
            throw new RuntimeException("User not found");
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void DeleteAll(){
        userRepository.deleteAll();
    }


}
