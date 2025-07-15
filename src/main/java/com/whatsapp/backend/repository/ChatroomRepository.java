package com.whatsapp.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.whatsapp.backend.entity.Chatroom;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
    @Query("SELECT cr FROM Chatroom cr JOIN cr.users cu WHERE cu.user.id = :userId")
    Page<Chatroom> findChatroomsByUserId(@Param("userId") Long userId, Pageable pageable);

	Page<Chatroom> findByUsers_UserId(Long userId, Pageable pageable);
}