package com.whatsapp.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MessageDTO {
    private Long id;
    private Long chatroomId;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;
    private AttachmentDTO attachment;
    private List<ReactionDTO> reactions;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getChatroomId() {
		return chatroomId;
	}
	public void setChatroomId(Long chatroomId) {
		this.chatroomId = chatroomId;
	}
	public Long getSenderId() {
		return senderId;
	}
	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public AttachmentDTO getAttachment() {
		return attachment;
	}
	public void setAttachment(AttachmentDTO attachment) {
		this.attachment = attachment;
	}
	public List<ReactionDTO> getReactions() {
		return reactions;
	}
	public void setReactions(List<ReactionDTO> reactions) {
		this.reactions = reactions;
	}
	
}