package org.example.mongodb.document;

import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;

import org.springframework.content.common.repository.ContentStore;

@ContentStoreRestResource(path="documents")
public interface XContentContentStore extends ContentStore<XContent, String> {

}
