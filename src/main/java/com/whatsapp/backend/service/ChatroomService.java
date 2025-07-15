package com.whatsapp.backend.service;

import com.whatsapp.backend.dto.ChatroomDTO;
import com.whatsapp.backend.dto.PaginatedResponse;
import com.whatsapp.backend.entity.Chatroom;
import com.whatsapp.backend.entity.ChatroomUser;
import com.whatsapp.backend.entity.User;
import com.whatsapp.backend.exception.ChatroomException;
import com.whatsapp.backend.exception.UserException;
import com.whatsapp.backend.repository.ChatroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatroomService {

	@Autowired
	private ChatroomRepository chatroomRepository;

	@Autowired
	private UserService userService;

	public ChatroomDTO createChatroom(ChatroomDTO chatroomDTO, Long creatorId) throws UserException, ChatroomException {
		User creator = userService.findUserById(creatorId);

		Chatroom chatroom = new Chatroom();
		chatroom.setName(chatroomDTO.getName());
		chatroom.setGroup(chatroomDTO.isGroup());
		chatroom.setCreatedBy(creator);

		Chatroom savedChatroom = chatroomRepository.save(chatroom);
		addUserToChatroom(savedChatroom.getId(), creatorId);

		if (chatroomDTO.getUserIds() != null) {
			for (Long userId : chatroomDTO.getUserIds()) {
				if (!userId.equals(creatorId)) {
					addUserToChatroom(savedChatroom.getId(), userId);
				}
			}
		}

		return mapChatroomToDTO(savedChatroom);
	}

	public void addUserToChatroom(Long chatroomId, Long userId) throws UserException, ChatroomException {
		Chatroom chatroom = chatroomRepository.findById(chatroomId)
				.orElseThrow(() -> new ChatroomException("Chatroom not found"));
		User user = userService.findUserById(userId);

		ChatroomUser chatroomUser = new ChatroomUser();
		chatroomUser.setChatroom(chatroom);
		chatroomUser.setUser(user);

		chatroom.getUsers().add(chatroomUser);
		chatroomRepository.save(chatroom);
	}

	public PaginatedResponse<ChatroomDTO> getChatrooms(Pageable pageable) {
		Page<Chatroom> chatrooms = chatroomRepository.findAll(pageable);

		List<ChatroomDTO> content = chatrooms.getContent().stream().map(this::convertToDto)
				.collect(Collectors.toList());

		return new PaginatedResponse<>(content, chatrooms.getNumber(), chatrooms.getSize(),
				chatrooms.getTotalElements());
	}

	private ChatroomDTO convertToDto(Chatroom chatroom) {
		ChatroomDTO dto = new ChatroomDTO();
		dto.setId(chatroom.getId());
		dto.setName(chatroom.getName());
		dto.setGroup(chatroom.isGroup());
		dto.setCreatedBy(chatroom.getCreatedBy() != null ? chatroom.getCreatedBy().getId() : null);

		List<Long> userIds = chatroom.getUsers().stream().map(cu -> cu.getUser().getId()).collect(Collectors.toList());
		dto.setUserIds(userIds);

		return dto;
	}

	public ChatroomDTO getChatroomDetails(Long chatroomId) throws ChatroomException {
		Chatroom chatroom = chatroomRepository.findById(chatroomId)
				.orElseThrow(() -> new ChatroomException("Chatroom not found"));
		return mapChatroomToDTO(chatroom);
	}

	public void removeUserFromChatroom(Long chatroomId, Long userId) throws ChatroomException, UserException {
		Chatroom chatroom = chatroomRepository.findById(chatroomId)
				.orElseThrow(() -> new ChatroomException("Chatroom not found"));

		chatroom.getUsers().removeIf(cu -> cu.getUser().getId().equals(userId));
		chatroomRepository.save(chatroom);
	}

	private ChatroomDTO mapChatroomToDTO(Chatroom chatroom) {
		ChatroomDTO dto = new ChatroomDTO();
		dto.setId(chatroom.getId());
		dto.setName(chatroom.getName());
		dto.setGroup(chatroom.isGroup());
		dto.setCreatedBy(chatroom.getCreatedBy() != null ? chatroom.getCreatedBy().getId() : null);

		List<Long> userIds = chatroom.getUsers().stream().map(cu -> cu.getUser().getId()).collect(Collectors.toList());
		dto.setUserIds(userIds);

		return dto;
	}
}