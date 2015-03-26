package org.springframework.content.examples;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "claims", path = "claims")
public interface ClaimRepository extends MongoRepository<Claim, String> {

}
