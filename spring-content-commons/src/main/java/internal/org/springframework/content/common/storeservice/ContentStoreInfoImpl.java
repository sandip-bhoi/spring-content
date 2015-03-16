package internal.org.springframework.content.common.storeservice;

import org.springframework.content.common.repository.ContentStore;
import org.springframework.content.common.storeservice.ContentStoreInfo;

public class ContentStoreInfoImpl implements ContentStoreInfo {
	
	private Class<? extends ContentStore> storeInterface;
	private Class<?> storeDomainClass;
	private ContentStore storeImpl;
	
	public ContentStoreInfoImpl(Class<? extends ContentStore> storeInterface, Class<?> storeDomainClass, ContentStore storeImpl) {
		this.storeInterface = storeInterface;
		this.storeDomainClass = storeDomainClass;
		this.storeImpl = storeImpl;
	}

	@Override
	public Class<? extends ContentStore> getInterface() {
		return this.storeInterface;
	}

	@Override
	public Class getDomainObjectClass() {
		return this.storeDomainClass;
	}

	@Override
	public ContentStore getImpementation() {
		return this.storeImpl;
	}
	
}
