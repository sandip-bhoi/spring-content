package org.springframework.content.common.repository.factory;

import org.springframework.content.common.repository.ContentStore;

public interface ContentStoreFactory {

	public Class<? extends ContentStore> getContentStoreInterface();
	public ContentStore getContentStore();
	
}
