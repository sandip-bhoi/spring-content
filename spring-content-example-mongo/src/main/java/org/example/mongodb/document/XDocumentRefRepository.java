package org.example.mongodb.document;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "documentrefs", path = "documentrefs")
public interface XDocumentRefRepository extends MongoRepository<XDocumentRef, String>/*, ContentStore<XContent, String>*/ {

}
