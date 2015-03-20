package org.springframework.content.common.config;

import internal.org.springframework.content.common.storeservice.ContentStoreServiceImpl;
import internal.org.springframework.content.common.utils.ContentRepositoryUtils;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public abstract class AbstractContentStoreBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

	private ResourceLoader resourceLoader;
	
	/* (non-Javadoc)
	 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
	 */
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		Assert.notNull(importingClassMetadata, "AnnotationMetadata must not be null!");
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

		// Guard against calls for sub-classes
		if (importingClassMetadata.getAnnotationAttributes(getAnnotation().getName()) == null) {
			return;
		}
		
		AnnotationAttributes attributes = new AnnotationAttributes(importingClassMetadata.getAnnotationAttributes(getAnnotation().getName()));
		
		String[] basePackages = ContentRepositoryUtils.getBasePackages(attributes, /* default*/ new String[] { ClassUtils.getPackageName(importingClassMetadata.getClassName()) });
		Set<GenericBeanDefinition> definitions = ContentRepositoryUtils.getContentRepositoryCandidates(resourceLoader, basePackages);

		BeanDefinition storeServiceBeanDef = createContentStoreServiceBeanDefinition(definitions);
		registry.registerBeanDefinition("contentStoreService", storeServiceBeanDef);

		for (BeanDefinition definition : definitions) {
		
			String factoryBeanName = ContentRepositoryUtils.getRepositoryFactoryBeanName(attributes);

			BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(factoryBeanName);

			builder.getRawBeanDefinition().setSource(importingClassMetadata);
			builder.addPropertyValue("contentStoreInterface", definition.getBeanClassName());
			
			registry.registerBeanDefinition(ContentRepositoryUtils.getRepositoryBeanName(definition), builder.getBeanDefinition());
		}
	}
	
	private BeanDefinition createContentStoreServiceBeanDefinition(Set<GenericBeanDefinition> storeBeanDefs) {
		GenericBeanDefinition beanDef = new GenericBeanDefinition();
		beanDef.setBeanClass(ContentStoreServiceImpl.class);

		MutablePropertyValues values = new MutablePropertyValues();
		beanDef.setPropertyValues(values);
		
		return beanDef;
	}

	/**
	 * Return the annotation to obtain configuration information from. Will be wrappen into an
	 * {@link AnnotationRepositoryConfigurationSource} so have a look at the constants in there for what annotation
	 * attributes it expects.
	 * 
	 * @return
	 */
	protected abstract Class<? extends Annotation> getAnnotation();
}
