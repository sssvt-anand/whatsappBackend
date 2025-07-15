package com.whatsapp.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whatsapp.backend.entity.Reaction;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
	Optional<Reaction> findByMessageIdAndUserId(Long messageId, Long userId);
}
