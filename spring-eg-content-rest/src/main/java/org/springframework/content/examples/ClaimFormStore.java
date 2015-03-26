package org.springframework.content.examples;

import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;

import org.springframework.content.common.repository.ContentStore;

@ContentStoreRestResource(path="claims")
public interface ClaimFormStore extends ContentStore<ClaimForm, String> {

}
