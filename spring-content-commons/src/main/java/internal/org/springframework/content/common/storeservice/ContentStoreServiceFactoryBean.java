package internal.org.springframework.content.common.storeservice;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.content.common.storeservice.ContentStoreService;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

public class ContentStoreServiceFactoryBean implements FactoryBean<ContentStoreService>, ResourceLoaderAware {

	private ContentStoreService storeService = null;
	private ResourceLoader loader = null;
	private Iterable<String> basePackages;
	
	@Override
	public ContentStoreService getObject() throws Exception {
		if (storeService == null)
			storeService = new ContentStoreServiceImpl();
		return storeService;
	}

	@Override
	public Class<?> getObjectType() {
		return ContentStoreService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.loader = resourceLoader;
	}
	
	protected void setBasePackages(Iterable<String> basePackages) {
		this.basePackages = basePackages;
	}
}
