package org.example.mongodb.document;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "documents", path = "documents")
public interface XDocumentRepository extends MongoRepository<XDocument, String>/*, ContentStore<XContent, String>*/ {

	public XDocument findByTitle(String name);
	
}
