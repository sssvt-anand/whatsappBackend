package com.whatsapp.backend.service;

import com.whatsapp.backend.dto.UserDTO;
import com.whatsapp.backend.entity.User;
import com.whatsapp.backend.exception.UserException;
import com.whatsapp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findUserById(Long userId) throws UserException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found with id: " + userId));
    }

    public UserDTO findUserProfile(Long userId) throws UserException {
        User user = findUserById(userId);
        return mapUserToDTO(user);
    }

    public UserDTO updateUser(Long userId, UserDTO userDTO) throws UserException {
        User user = findUserById(userId);
        
        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getProfilePicture() != null) {
            user.setProfilePictureUrl(userDTO.getProfilePicture());
        }
        if (userDTO.getStatus() != null) {
            user.setStatus(userDTO.getStatus());
        }
        
        User updatedUser = userRepository.save(user);
        return mapUserToDTO(updatedUser);
    }

    public void updateUserLastSeen(Long userId) throws UserException {
        User user = findUserById(userId);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
    }

    public UserDTO registerUser(String phoneNumber, String name) {
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setName(name);
        user.setStatus("Hey there! I'm using WhatsApp");
        
        User savedUser = userRepository.save(user);
        return mapUserToDTO(savedUser);
    }

    private UserDTO mapUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setName(user.getName());
        userDTO.setProfilePicture(user.getProfilePictureUrl());
        userDTO.setStatus(user.getStatus());
        userDTO.setLastSeen(user.getLastSeen() != null ? user.getLastSeen().toString() : null);
        return userDTO;
    }
}