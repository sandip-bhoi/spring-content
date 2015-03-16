package org.example.mongodb.document;

import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;

import org.springframework.content.common.repository.ContentStore;

@ContentStoreRestResource(path="documentrefs")
public interface XContent2ContentStore extends ContentStore<XContent2, String> {

}
