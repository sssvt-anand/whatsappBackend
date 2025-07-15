package com.whatsapp.backend.repository;

import com.whatsapp.backend.entity.Message;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	Page<Message> findByChatroomIdOrderByCreatedAtDesc(Long chatroomId, Pageable pageable);

	Page<Message> findByChatroomIdAndIdLessThanOrderByCreatedAtDesc(Long chatroomId, Long messageId, Pageable pageable);

	Page<Message> findByChatroomId(Long chatroomId, Pageable pageable);

	Page<Message> findByChatroomIdAndCreatedAtBefore(Long chatroomId, LocalDateTime createdAt, Pageable pageable);
}
