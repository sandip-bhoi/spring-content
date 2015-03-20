package org.springframework.content.common.repository.factory;

import java.io.Serializable;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.content.common.repository.ContentStore;
import org.springframework.util.Assert;

public abstract class AbstractContentStoreFactoryBean<T extends ContentStore<S, ID>, S, ID extends Serializable>
	implements InitializingBean, FactoryBean<T>, BeanClassLoaderAware, ContentStoreFactory {

	//private AbstractContentStoreFactory factory;

	//private ContentStoreService contentStoreService;
	private Class<? extends ContentStore<Object, Serializable>> contentStoreInterface;
	private ClassLoader classLoader;
	
	private T contentStore;

	/*public ContentStoreService getContentStoreService() {
		return contentStoreService;
	}

	@Autowired
	public void setContentStoreService(ContentStoreService contentStoreService) {
		this.contentStoreService = contentStoreService;
	}*/

	@Required
	public void setContentStoreInterface(Class<? extends ContentStore<Object, Serializable>> contentStoreInterface) {
		Assert.notNull(contentStoreInterface);
		this.contentStoreInterface = contentStoreInterface;
	}
	
	public Class<? extends ContentStore<Object, Serializable>> getContentStoreInterface() {
		return this.contentStoreInterface;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@SuppressWarnings("unchecked")
	public ContentStore<Object,Serializable> getContentStore() {
		return (ContentStore<Object, Serializable>) getObject();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	public T getObject() {
		return initAndReturn();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends T> getObjectType() {
		return (Class<? extends T>) (null == contentStoreInterface ? ContentStore.class : contentStoreInterface);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		initAndReturn();
	}

	private T initAndReturn() {
		if (contentStore == null) {
			contentStore = createContentStore();
		}
		return contentStore;
	}

	@SuppressWarnings("unchecked")
	private T createContentStore() {
		Object target = getContentStoreImpl();

		// Create proxy
		ProxyFactory result = new ProxyFactory();
		result.setTarget(target);
		result.setInterfaces(new Class[] { contentStoreInterface, ContentStore.class });

		return (T)result.getProxy(classLoader);
	}

	protected abstract Object getContentStoreImpl();
}
