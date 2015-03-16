package internal.org.springframework.content.rest.utils;

import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;

import org.springframework.content.common.storeservice.ContentStoreInfo;
import org.springframework.content.common.storeservice.ContentStoreService;

public final class ContentStoreUtils {

	private ContentStoreUtils() {}
	
	public static ContentStoreInfo findContentStore(ContentStoreService stores, String repository) {
		
		for (ContentStoreInfo info : stores.getContentStores()) {
			ContentStoreRestResource restResource = (ContentStoreRestResource) info.getInterface().getAnnotation(ContentStoreRestResource.class);
			if (restResource != null && restResource.path().equals(repository))
				return info;
		}
		return null;
	}

}
