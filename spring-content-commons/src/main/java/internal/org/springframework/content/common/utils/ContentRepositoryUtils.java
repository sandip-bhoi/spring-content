package internal.org.springframework.content.common.utils;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.content.common.config.ContentRepositoryConfiguration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

public class ContentRepositoryUtils {

	public static Set<GenericBeanDefinition> getContentRepositoryCandidates(ResourceLoader loader, Iterable<String> basePackages) {
		ContentRepositoryCandidateComponentProvider scanner = new ContentRepositoryCandidateComponentProvider(false);
		//scanner.setConsiderNestedRepositoryInterfaces(shouldConsiderNestedRepositories());
		scanner.setResourceLoader(loader);
		//scanner.setEnvironment(environment);

		/*for (TypeFilter filter : getExcludeFilters()) {
			scanner.addExcludeFilter(filter);
		}*/

		Set<GenericBeanDefinition> result = new HashSet<>();

		for (String basePackage : basePackages) {
			Set<BeanDefinition> candidates = scanner.findCandidateComponents(basePackage);
			for (BeanDefinition candidate : candidates)
				result.add((GenericBeanDefinition)candidate);
		}

		return result;	
	}
	
	public static BeanDefinitionBuilder buildContentRepositoryBeanDefinitionBuilder(ResourceLoader resourceLoader, ContentRepositoryConfiguration<?> configuration) {

		Assert.notNull(resourceLoader, "ResourceLoader must not be null!");
		Assert.notNull(configuration, "ContentRepositoryConfiguration<?> must not be null!");

		String factoryBeanName = configuration.getRepositoryFactoryBeanName();
		/*factoryBeanName = StringUtils.hasText(factoryBeanName) ? factoryBeanName : extension
				.getRepositoryFactoryClassName();*/

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(factoryBeanName);

		builder.getRawBeanDefinition().setSource(configuration.getSource());
		builder.addPropertyValue("contentStoreInterface", configuration.getRepositoryInterface());
		//builder.addPropertyValue("contentStoreService", new RuntimeBeanNameReference("contentStoreService"));
		//builder.addPropertyValue("queryLookupStrategyKey", configuration.getQueryLookupStrategyKey());
		//builder.addPropertyValue("lazyInit", configuration.isLazyInit());

		/*NamedQueriesBeanDefinitionBuilder definitionBuilder = new NamedQueriesBeanDefinitionBuilder(
				extension.getDefaultNamedQueryLocation());

		if (StringUtils.hasText(configuration.getNamedQueriesLocation())) {
			definitionBuilder.setLocations(configuration.getNamedQueriesLocation());
		}

		builder.addPropertyValue("namedQueries", definitionBuilder.build(configuration.getSource()));

		String customImplementationBeanName = registerCustomImplementation(configuration);

		if (customImplementationBeanName != null) {
			builder.addPropertyReference("customImplementation", customImplementationBeanName);
			builder.addDependsOn(customImplementationBeanName);
		}*/

		/*RootBeanDefinition evaluationContextProviderDefinition = new RootBeanDefinition(ExtensionAwareEvaluationContextProvider.class);
		evaluationContextProviderDefinition.setSource(configuration.getSource());

		builder.addPropertyValue("evaluationContextProvider", evaluationContextProviderDefinition);*/

		return builder;
		
	}
}
