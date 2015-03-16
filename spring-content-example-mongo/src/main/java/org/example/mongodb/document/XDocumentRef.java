package org.example.mongodb.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.content.annotations.Content;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class XDocumentRef {
	
	@Id private String id;
	private String name;
	@Content private List<XContent2> images = new ArrayList<>();
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<XContent2> getImages() {
		return images;
	}

	public void setImages(List<XContent2> images) {
		this.images = images;
	}

}
