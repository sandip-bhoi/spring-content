package internal.com.emc.spring.content.commons.utils;

import java.beans.Introspector;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.emc.spring.content.commons.config.ContentRepositoriesConfigurationSource;
import com.emc.spring.content.commons.config.ContentRepositoryConfiguration;

public class DefaultContentRepositoryConfiguration<T extends ContentRepositoriesConfigurationSource> 
				implements ContentRepositoryConfiguration<T> {

	private final T configurationSource;
	private final BeanDefinition definition;

	/**
	 * Creates a new {@link DefaultRepositoryConfiguration} from the given {@link RepositoryConfigurationSource} and
	 * source {@link BeanDefinition}.
	 * 
	 * @param configurationSource must not be {@literal null}.
	 * @param definition must not be {@literal null}.
	 */
	public DefaultContentRepositoryConfiguration(T configurationSource, BeanDefinition definition) {

		Assert.notNull(configurationSource);
		Assert.notNull(definition);

		this.configurationSource = configurationSource;
		this.definition = definition;
	}

	public String getRepositoryBeanName() {
		String beanName = ClassUtils.getShortName(definition.getBeanClassName());
		return Introspector.decapitalize(beanName);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getRepositoryFactoryBeanName()
	 */
	public String getRepositoryFactoryBeanName() {
		return configurationSource.getRepositoryFactoryBeanName();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getRepositoryInterface()
	 */
	public String getRepositoryInterface() {
		return definition.getBeanClassName();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getSource()
	 */
	public Object getSource() {
		return configurationSource.getSource();
	}
}