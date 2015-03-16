package internal.org.springframework.content.rest.config;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import internal.org.springframework.content.rest.annotations.ContentRestController;
import internal.org.springframework.content.rest.mappings.ContentHandlerMapping;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.annotations.Content;
import org.springframework.content.annotations.ContentId;
import org.springframework.content.common.storeservice.ContentStoreService;
import org.springframework.content.common.utils.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.invoke.RepositoryInvokerFactory;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@ComponentScan(basePackageClasses = ContentRestController.class)
public class ContentRestConfiguration {
	
	@Autowired
	Repositories repositories;
	
	@Autowired
	RepositoryInvokerFactory repositoryInvokerFactory;
	
	@Autowired
	ResourceMappings repositoryMappings;
	
	@Autowired 
	ContentStoreService storeService;
	
	@Bean
	RequestMappingHandlerMapping contentHandlerMapping() {
		return new ContentHandlerMapping(storeService, repositories, repositoryInvokerFactory, repositoryMappings);
	}
	
	@Bean 
	public ResourceProcessor<PersistentEntityResource> contentLinksProcessor() {
        return new ResourceProcessor<PersistentEntityResource>() {

        	
			public PersistentEntityResource process(final PersistentEntityResource resource) {
				
				Object object = resource.getContent();
				BeanWrapper wrapper = new BeanWrapperImpl(object);
				for (PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {
					Field field;
					try {
						field = object.getClass().getDeclaredField(descriptor.getName());
						if (!field.isAnnotationPresent(Content.class)) 
							continue;
					} catch (NoSuchFieldException e) {
						continue;
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Object propVal = wrapper.getPropertyValue(descriptor.getName());
					PersistentProperty<?> prop = resource.getPersistentEntity().getPersistentProperty(descriptor.getName());
					if (prop.isArray() || prop.isCollectionLike()) {
						resource.add(new Link(resource.getLink("self").getHref() + "/" + descriptor.getName(), descriptor.getName()));
					} else {
						if (propVal != null) {
							String id = BeanUtils.getFieldWithAnnotation(propVal, ContentId.class).toString();
							Assert.notNull(id);
							resource.add(new Link(resource.getLink("self").getHref() + "/" + descriptor.getName() + "/" + id, descriptor.getName()));
						}
					}
				}

				return resource;
			}
        };
    }

}
