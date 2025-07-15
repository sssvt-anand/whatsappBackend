package com.whatsapp.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whatsapp.backend.entity.Attachment;
@Repository
public interface AttachmentRepository  extends JpaRepository<Attachment, Long>{

}
