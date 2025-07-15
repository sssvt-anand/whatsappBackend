package com.whatsapp.backend.dto;

import com.whatsapp.backend.entity.Attachment.FileType;
import lombok.Data;

@Data
public class AttachmentDTO {
    private Long id;
    private String filePath;
    private FileType fileType;
    private long fileSize;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public FileType getFileType() {
		return fileType;
	}
	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	
}