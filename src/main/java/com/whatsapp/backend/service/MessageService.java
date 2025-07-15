package com.whatsapp.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.whatsapp.backend.dto.AttachmentDTO;
import com.whatsapp.backend.dto.MessageDTO;
import com.whatsapp.backend.dto.PaginatedResponse;
import com.whatsapp.backend.dto.ReactionDTO;
import com.whatsapp.backend.entity.Attachment;
import com.whatsapp.backend.entity.Chatroom;
import com.whatsapp.backend.entity.Message;
import com.whatsapp.backend.entity.Reaction;
import com.whatsapp.backend.entity.User;
import com.whatsapp.backend.exception.ChatroomException;
import com.whatsapp.backend.exception.MessageException;
import com.whatsapp.backend.exception.UserException;
import com.whatsapp.backend.repository.AttachmentRepository;
import com.whatsapp.backend.repository.ChatroomRepository;
import com.whatsapp.backend.repository.MessageRepository;
import com.whatsapp.backend.repository.ReactionRepository;

@Service
public class MessageService {

	@Autowired
	private MessageRepository messageRepository;
	@Autowired
	private ChatroomRepository chatroomRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private AttachmentRepository attachmentRepository;
	@Autowired
	private ReactionRepository reactionRepository;

	@Value("${file.upload-dir}")
	private String uploadDir;

	public MessageDTO sendMessage(Long chatroomId, Long senderId, String content)
			throws UserException, ChatroomException {
		User sender = userService.findUserById(senderId);
		Chatroom chatroom = chatroomRepository.findById(chatroomId)
				.orElseThrow(() -> new ChatroomException("Chatroom not found"));

		Message message = new Message();
		message.setChatroom(chatroom);
		message.setSender(sender);
		message.setContent(content);
		message.setCreatedAt(LocalDateTime.now());

		Message savedMessage = messageRepository.save(message);
		return mapMessageToDTO(savedMessage);
	}

	public MessageDTO sendMessageWithAttachment(Long chatroomId, Long senderId, MultipartFile file)
			throws UserException, ChatroomException, IOException {
		if (file.getSize() > 10 * 1024 * 1024) {
			throw new IllegalArgumentException("File size exceeds 10MB limit");
		}

		User sender = userService.findUserById(senderId);
		Chatroom chatroom = chatroomRepository.findById(chatroomId)
				.orElseThrow(() -> new ChatroomException("Chatroom not found"));

		Message message = new Message();
		message.setChatroom(chatroom);
		message.setSender(sender);
		message.setCreatedAt(LocalDateTime.now());

		String originalFilename = file.getOriginalFilename();
		String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String uniqueFileName = UUID.randomUUID() + fileExtension;
		Attachment.FileType fileType = determineFileType(file.getContentType());
		String subDirectory = fileType.name().toLowerCase() + "s";
		String filePath = uploadDir + "/" + subDirectory + "/" + uniqueFileName;

		Files.createDirectories(Paths.get(uploadDir + "/" + subDirectory));
		Files.copy(file.getInputStream(), Paths.get(filePath));

		Attachment attachment = new Attachment();
		attachment.setFilePath(filePath);
		attachment.setFileType(fileType);
		attachment.setFileSize(file.getSize());
		attachment.setMessage(message);

		message.setAttachment(attachment);
		Message savedMessage = messageRepository.save(message);
		return mapMessageToDTO(savedMessage);
	}

	public PaginatedResponse<MessageDTO> getChatroomMessagesBefore(Long chatroomId, Long beforeMessageId, int page,
			int size) throws MessageException {
		// Create pageable with sorting by createdAt descending
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		// Get messages before the specified message ID
		Page<Message> messages;
		if (beforeMessageId != null) {
			Message beforeMessage = messageRepository.findById(beforeMessageId)
					.orElseThrow(() -> new MessageException("Reference message not found"));
			messages = messageRepository.findByChatroomIdAndCreatedAtBefore(chatroomId, beforeMessage.getCreatedAt(),
					pageable);
		} else {
			messages = messageRepository.findByChatroomId(chatroomId, pageable);
		}

		// Convert to DTOs
		List<MessageDTO> content = messages.getContent().stream().map(this::mapMessageToDTO)
				.collect(Collectors.toList());

		return new PaginatedResponse<>(content, messages.getNumber(), messages.getSize(), messages.getTotalElements());
	}

	public ReactionDTO addReaction(Long messageId, Long userId, Reaction.EmojiType emojiType)
			throws MessageException, UserException {
		Message message = messageRepository.findById(messageId)
				.orElseThrow(() -> new MessageException("Message not found"));
		User user = userService.findUserById(userId);

		Optional<Reaction> existingReaction = reactionRepository.findByMessageIdAndUserId(messageId, userId);
		Reaction reaction = existingReaction.orElseGet(Reaction::new);

		reaction.setMessage(message);
		reaction.setUser(user);
		reaction.setEmojiType(emojiType);

		Reaction savedReaction = reactionRepository.save(reaction);
		return mapReactionToDTO(savedReaction);
	}

	public void removeReaction(Long messageId, Long userId) throws MessageException {
		Reaction reaction = reactionRepository.findByMessageIdAndUserId(messageId, userId)
				.orElseThrow(() -> new MessageException("Reaction not found"));
		reactionRepository.delete(reaction);
	}

	public void deleteMessage(Long messageId, Long userId) throws MessageException, UserException {
		Message message = messageRepository.findById(messageId)
				.orElseThrow(() -> new MessageException("Message not found"));

		if (!message.getSender().getId().equals(userId)) {
			throw new UserException("You can only delete your own messages");
		}

		if (message.getAttachment() != null) {
			try {
				Files.deleteIfExists(Paths.get(message.getAttachment().getFilePath()));
			} catch (IOException e) {
				// Log error but continue with deletion
			}
		}

		messageRepository.delete(message);
	}

	private Attachment.FileType determineFileType(String contentType) {
		if (contentType == null)
			return Attachment.FileType.DOCUMENT;
		if (contentType.startsWith("image/"))
			return Attachment.FileType.IMAGE;
		if (contentType.startsWith("video/"))
			return Attachment.FileType.VIDEO;
		return Attachment.FileType.DOCUMENT;
	}

	private MessageDTO mapMessageToDTO(Message message) {
		MessageDTO dto = new MessageDTO();
		dto.setId(message.getId());
		dto.setChatroomId(message.getChatroom().getId());
		dto.setSenderId(message.getSender().getId());
		dto.setContent(message.getContent());
		dto.setCreatedAt(message.getCreatedAt());

		if (message.getAttachment() != null) {
			dto.setAttachment(mapAttachmentToDTO(message.getAttachment()));
		}

		if (message.getReactions() != null && !message.getReactions().isEmpty()) {
			dto.setReactions(message.getReactions().stream().map(this::mapReactionToDTO).collect(Collectors.toList()));
		}

		return dto;
	}

	private AttachmentDTO mapAttachmentToDTO(Attachment attachment) {
		AttachmentDTO dto = new AttachmentDTO();
		dto.setId(attachment.getId());
		dto.setFilePath(attachment.getFilePath());
		dto.setFileType(attachment.getFileType());
		dto.setFileSize(attachment.getFileSize());
		return dto;
	}

	private ReactionDTO mapReactionToDTO(Reaction reaction) {
		ReactionDTO dto = new ReactionDTO();
		dto.setId(reaction.getId());
		dto.setMessageId(reaction.getMessage().getId());
		dto.setUserId(reaction.getUser().getId());
		dto.setEmojiType(reaction.getEmojiType());
		return dto;
	}

}