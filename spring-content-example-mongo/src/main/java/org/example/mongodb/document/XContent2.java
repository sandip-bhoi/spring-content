package org.example.mongodb.document;

import org.springframework.content.annotations.ContentId;
import org.springframework.content.annotations.ContentLength;
import org.springframework.content.annotations.MimeType;

public class XContent2 {

	@ContentId private String contentId;
	@MimeType private String mimeType;
	@ContentLength private String contentLen;
	
	public String getContentId() {
		return contentId;
	}
	
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public String getContentLen() {
		return contentLen;
	}
	
	public void setContentLen(String contentLen) {
		this.contentLen = contentLen;
	}
}
