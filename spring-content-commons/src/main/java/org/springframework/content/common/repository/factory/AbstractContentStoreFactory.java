package org.springframework.content.common.repository.factory;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.content.common.repository.ContentStore;


public class AbstractContentStoreFactory<T extends ContentStore> {
	
	public <T> T getContentStoreImpl(Class<T> contentStoreInterface) {
		return null;
		
		/*Object target = getTargetRepository(information);

		// Create proxy
		ProxyFactory result = new ProxyFactory();
		result.setTarget(target);
		result.setInterfaces(new Class[] { contentStoreInterface, ContentStore.class });

		for (RepositoryProxyPostProcessor processor : postProcessors) {
			processor.postProcess(result, information);
		}

		if (IS_JAVA_8) {
			result.addAdvice(new DefaultMethodInvokingMethodInterceptor());
		}

		result.addAdvice(new QueryExecutorMethodInterceptor(information, customImplementation, target));

		return (T) result.getProxy(classLoader);*/	
	}
	
}
