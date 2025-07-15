package com.whatsapp.backend.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whatsapp.backend.dto.MessageDTO;
import com.whatsapp.backend.dto.PaginatedResponse;
import com.whatsapp.backend.dto.ReactionDTO;
import com.whatsapp.backend.entity.Reaction.EmojiType;
import com.whatsapp.backend.exception.ChatroomException;
import com.whatsapp.backend.exception.MessageException;
import com.whatsapp.backend.exception.UserException;
import com.whatsapp.backend.service.MessageService;

@RestController
@RequestMapping("/api/chatrooms/{chatroomId}/messages")
public class MessageController {

	@Autowired
	private MessageService messageService;

	@PostMapping
	public ResponseEntity<MessageDTO> sendMessage(@PathVariable Long chatroomId, @RequestHeader("userId") Long senderId,
			@RequestParam(required = false) String content, @RequestParam(required = false) MultipartFile file)
			throws UserException, ChatroomException, IOException {

		if (file != null) {
			MessageDTO messageDTO = messageService.sendMessageWithAttachment(chatroomId, senderId, file);
			return ResponseEntity.ok(messageDTO);
		} else {
			MessageDTO messageDTO = messageService.sendMessage(chatroomId, senderId, content);
			return ResponseEntity.ok(messageDTO);
		}
	}

	@GetMapping
	public ResponseEntity<PaginatedResponse<MessageDTO>> getMessages(@PathVariable Long chatroomId,
			@RequestParam(required = false) Long beforeMessageId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) throws ChatroomException, MessageException {
		PaginatedResponse<MessageDTO> response = messageService.getChatroomMessagesBefore(chatroomId, beforeMessageId,
				page, size);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/before")
	public ResponseEntity<PaginatedResponse<MessageDTO>> getMessagesBefore(@PathVariable Long chatroomId,
			@RequestParam Long beforeMessageId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) throws ChatroomException, MessageException {
		PaginatedResponse<MessageDTO> response = messageService.getChatroomMessagesBefore(chatroomId, beforeMessageId,
				page, size);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{messageId}/reactions")
	public ResponseEntity<ReactionDTO> addReaction(@PathVariable Long chatroomId, @PathVariable Long messageId,
			@RequestHeader("userId") Long userId, @RequestParam EmojiType emojiType)
			throws MessageException, UserException {
		ReactionDTO reactionDTO = messageService.addReaction(messageId, userId, emojiType);
		return ResponseEntity.ok(reactionDTO);
	}

	@DeleteMapping("/{messageId}/reactions")
	public ResponseEntity<Void> removeReaction(@PathVariable Long chatroomId, @PathVariable Long messageId,
			@RequestHeader("userId") Long userId) throws MessageException, UserException {
		messageService.removeReaction(messageId, userId);
		return ResponseEntity.ok().build();
	}
}
