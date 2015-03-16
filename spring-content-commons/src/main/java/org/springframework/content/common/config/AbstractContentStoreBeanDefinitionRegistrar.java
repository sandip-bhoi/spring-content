package org.springframework.content.common.config;

import internal.org.springframework.content.common.storeservice.ContentStoreServiceImpl;
import internal.org.springframework.content.common.utils.AnnotationBasedContentRepositoryConfigurationSource;
import internal.org.springframework.content.common.utils.ContentRepositoryUtils;
import internal.org.springframework.content.common.utils.DefaultContentRepositoryConfiguration;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

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
		
		AnnotationBasedContentRepositoryConfigurationSource source = new AnnotationBasedContentRepositoryConfigurationSource(importingClassMetadata, getAnnotation());
		Set<GenericBeanDefinition> definitions = ContentRepositoryUtils.getContentRepositoryCandidates(resourceLoader, source.getBasePackages());

		BeanDefinition storeServiceBeanDef = createContentStoreServiceBeanDefinition(definitions);
		registry.registerBeanDefinition("contentStoreService", storeServiceBeanDef);

		Collection<ContentRepositoryConfiguration<AnnotationBasedContentRepositoryConfigurationSource>> repositoryConfigurations = new HashSet<>();
		for (BeanDefinition definition : definitions) {
			ContentRepositoryConfiguration<AnnotationBasedContentRepositoryConfigurationSource> repoConfig = new DefaultContentRepositoryConfiguration<AnnotationBasedContentRepositoryConfigurationSource>(source, definition);
			repositoryConfigurations.add(repoConfig);
		}
		
		for (ContentRepositoryConfiguration<AnnotationBasedContentRepositoryConfigurationSource> repoConfig : repositoryConfigurations) {
			
			BeanDefinitionBuilder builder = ContentRepositoryUtils.buildContentRepositoryBeanDefinitionBuilder(resourceLoader, repoConfig);
			
			/*extension.postProcess(definitionBuilder, configurationSource);

			if (isXml) {
				extension.postProcess(definitionBuilder, (XmlRepositoryConfigurationSource) configurationSource);
			} else {
				extension.postProcess(definitionBuilder, (AnnotationRepositoryConfigurationSource) configurationSource);
			}*/

			AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
					
			/*if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(REPOSITORY_REGISTRATION, extension.getModuleName(), beanName,
						configuration.getRepositoryInterface(), extension.getRepositoryFactoryClassName());
			}*/

			registry.registerBeanDefinition(repoConfig.getRepositoryBeanName(), beanDefinition);
			//definitions.add(new BeanComponentDefinition(beanDefinition, beanName));
		}
	}
	
	private BeanDefinition createContentStoreServiceBeanDefinition(Set<GenericBeanDefinition> storeBeanDefs) {
		GenericBeanDefinition beanDef = new GenericBeanDefinition();
		beanDef.setBeanClass(ContentStoreServiceImpl.class);

		MutablePropertyValues values = new MutablePropertyValues();
		
		/*Set<ContentStoreInfo> infos = new HashSet<>();
		for (GenericBeanDefinition storeBeanDef : storeBeanDefs) {
			try {
				storeBeanDef.resolveBeanClass(this.getClass().getClassLoader());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			infos.add(new ContentStoreInfoImpl(storeBeanDef));
		}
		values.addPropertyValue("contentStoreInfos", infos);*/

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
