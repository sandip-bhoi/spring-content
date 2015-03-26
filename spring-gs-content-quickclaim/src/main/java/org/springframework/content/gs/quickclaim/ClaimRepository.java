package org.springframework.content.gs.quickclaim;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "xclaims", path = "xclaims")
public interface ClaimRepository extends MongoRepository<Claim, String> {

}
