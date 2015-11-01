package docs;

import org.springframework.content.common.repository.ContentStore;

import docs.SpringDocument.ContentMetadata;
import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;

@ContentStoreRestResource
public interface ContentMetadataStore extends ContentStore<ContentMetadata, String> {

}
