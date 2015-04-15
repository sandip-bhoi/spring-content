package org.springframework.content.gs.quickclaim;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "xclaims", path = "xclaims")
public interface ClaimRepository extends PagingAndSortingRepository<Claim, String> {

}
