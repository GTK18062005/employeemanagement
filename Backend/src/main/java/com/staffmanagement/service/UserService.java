package com.staffmanagement.service;

import com.staffmanagement.model.ProfileUpdateRequest;
import com.staffmanagement.model.User;
import com.staffmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public Optional<User> getUserProfile(String username) {
        return userRepository.findUserProfileByUsername(username);
    }
    
    public User updateUserProfile(String username, ProfileUpdateRequest updateRequest) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Update fields if they are provided in the request
            if (updateRequest.getName() != null) {
                user.setName(updateRequest.getName());
            }
            if (updateRequest.getEmail() != null) {
                user.setEmail(updateRequest.getEmail());
            }
            if (updateRequest.getPhone() != null) {
                user.setPhone(updateRequest.getPhone());
            }
            if (updateRequest.getDepartment() != null) {
                user.setDepartment(updateRequest.getDepartment());
            }
            if (updateRequest.getDesignation() != null) {
                user.setDesignation(updateRequest.getDesignation());
            }
            if (updateRequest.getAddress() != null) {
                user.setAddress(updateRequest.getAddress());
            }
            if (updateRequest.getEmergencyContact() != null) {
                user.setEmergencyContact(updateRequest.getEmergencyContact());
            }
            if (updateRequest.getBankAccountNumber() != null) {
                user.setBankAccountNumber(updateRequest.getBankAccountNumber());
            }
            if (updateRequest.getPanNumber() != null) {
                user.setPanNumber(updateRequest.getPanNumber());
            }
            
            return userRepository.save(user);
        }
        
        return null;
    }
    
    public boolean changePassword(String username, String currentPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsernameAndPassword(username, currentPassword);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
        
        return false;
    }
}