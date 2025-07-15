package com.whatsapp.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whatsapp.backend.dto.ChatroomDTO;
import com.whatsapp.backend.dto.PaginatedResponse;
import com.whatsapp.backend.exception.ChatroomException;
import com.whatsapp.backend.exception.UserException;
import com.whatsapp.backend.service.ChatroomService;

@RestController
@RequestMapping("/api/chatrooms")
public class ChatroomController {

	private final ChatroomService chatroomService;

	@Autowired
	public ChatroomController(ChatroomService chatroomService) {
		this.chatroomService = chatroomService;
	}

	@PostMapping
	public ResponseEntity<ChatroomDTO> createChatroom(@RequestBody ChatroomDTO chatroomDTO,
			@RequestHeader("userId") Long creatorId) throws UserException, ChatroomException {
		ChatroomDTO createdChatroom = chatroomService.createChatroom(chatroomDTO, creatorId);
		return ResponseEntity.ok(createdChatroom);
	}

	@PostMapping("/{chatroomId}/users/{userId}")
	public ResponseEntity<Void> addUserToChatroom(@PathVariable Long chatroomId, @PathVariable Long userId)
			throws UserException, ChatroomException {
		chatroomService.addUserToChatroom(chatroomId, userId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/all")
	public ResponseEntity<PaginatedResponse<ChatroomDTO>> getAllChatrooms(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		PaginatedResponse<ChatroomDTO> response = chatroomService.getChatrooms(pageable);
		return ResponseEntity.ok(response);
	}
}
