package org.springframework.content.gs.quickclaim;

import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;

import org.springframework.content.common.repository.ContentStore;

@ContentStoreRestResource(path="xclaims")
public interface ClaimFormStore extends ContentStore<ClaimForm, String> {

}
