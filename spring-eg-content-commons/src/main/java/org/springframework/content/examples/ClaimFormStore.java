package org.springframework.content.examples;

import org.springframework.content.common.renditions.Renderable;
import org.springframework.content.common.repository.ContentStore;

import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;

@ContentStoreRestResource(path="claims")
public interface ClaimFormStore extends ContentStore<ClaimForm, String>, Renderable<ClaimForm> {

}
