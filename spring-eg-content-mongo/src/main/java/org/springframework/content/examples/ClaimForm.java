package org.springframework.content.examples;

import org.springframework.content.annotations.ContentId;
import org.springframework.content.annotations.ContentLength;

public class ClaimForm {

	@ContentId
	private String contentId;
	
	@ContentLength
	private long contentLength;
	
	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
}
