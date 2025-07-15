package com.whatsapp.backend.dto;

import java.util.List;

public class PaginatedResponse<T> {
	private List<T> content;
	private int page;
	private int size;
	private long totalElements;

	public PaginatedResponse(List<T> content, int page, int size, long totalElements) {
		this.content = content;
		this.page = page;
		this.size = size;
		this.totalElements = totalElements;
	}

}