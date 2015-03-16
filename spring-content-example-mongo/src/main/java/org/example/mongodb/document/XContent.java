package org.example.mongodb.document;

import org.springframework.content.annotations.Content;
import org.springframework.content.annotations.ContentId;
import org.springframework.content.annotations.ContentLength;
import org.springframework.content.annotations.MimeType;

@Content
public class XContent /*implements Content*/ {

	@ContentId private String contentId;
	@MimeType private String mimeType;
	@ContentLength private long contentLen;
	
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
	
	public long getContentLen() {
		return contentLen;
	}
	
	public void setContentLen(long contentLen) {
		this.contentLen = contentLen;
	}
}
