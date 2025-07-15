package com.whatsapp.backend.dto;

import com.whatsapp.backend.entity.Reaction.EmojiType;
import lombok.Data;

@Data
public class ReactionDTO {
    private Long id;
    private Long messageId;
    private Long userId;
    private EmojiType emojiType;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMessageId() {
		return messageId;
	}
	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public EmojiType getEmojiType() {
		return emojiType;
	}
	public void setEmojiType(EmojiType emojiType) {
		this.emojiType = emojiType;
	}
	
}