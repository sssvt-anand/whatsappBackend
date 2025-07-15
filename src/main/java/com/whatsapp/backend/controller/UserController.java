package com.whatsapp.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whatsapp.backend.dto.UserDTO;
import com.whatsapp.backend.exception.UserException;
import com.whatsapp.backend.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/{userId}/profile")
	public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long userId) throws UserException {
		UserDTO userDTO = userService.findUserProfile(userId);
		return ResponseEntity.ok(userDTO);
	}

	@PutMapping("/{userId}/profile")
	public ResponseEntity<UserDTO> updateUserProfile(@PathVariable Long userId, @RequestBody UserDTO userDTO)
			throws UserException {
		UserDTO updatedUser = userService.updateUser(userId, userDTO);
		return ResponseEntity.ok(updatedUser);
	}

	@PostMapping("/{userId}/last-seen")
	public ResponseEntity<Void> updateLastSeen(@PathVariable Long userId) throws UserException {
		userService.updateUserLastSeen(userId);
		return ResponseEntity.ok().build();
	}

}
