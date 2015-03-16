package org.example.mongodb.document;

import org.springframework.content.annotations.Content;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class XDocument {

	@Id private String id;
	private String title;
	private String[] keywords;
	@Content private XContent content;
	@DBRef private XDocumentRef ref;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String[] getKeywords() {
		return keywords;
	}
	
	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}

	public XContent getContent() {
		return content;
	}

	public void setContent(XContent content) {
		this.content = content;
	}

	public XDocumentRef getRef() {
		return ref;
	}

	public void setRef(XDocumentRef ref) {
		this.ref = ref;
	}
	
}
