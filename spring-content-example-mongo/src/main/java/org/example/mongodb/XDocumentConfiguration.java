package org.example.mongodb;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import net.sf.ehcache.pool.sizeof.filter.PassThroughFilter;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.content.annotations.Content;
import org.springframework.content.annotations.ContentId;
import org.springframework.content.common.utils.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.util.Assert;

@Configuration
public class XDocumentConfiguration {

	/*@Bean public ResourceProcessor<Resource<XDocument>> personProcessor() {
        return new ResourceProcessor<Resource<XDocument>>() {

			public Resource<XDocument> process(Resource<XDocument> resource) {
				resource.add(new Link(resource.getLink("self").getHref() + "/content", "content"));
                return resource;
			}
        };
    }*/

	/*@Bean public <T extends Content> ResourceProcessor<Resource<T>> personProcessor() {
        return new ResourceProcessor<Resource<T>>() {

			public Resource<T> process(Resource<T> resource) {
				resource.add(new Link(resource.getLink("self").getHref() + "/content", "content"));
                return resource;
			}
        };
    }*/

	/*@Bean public ResourceProcessor<Resource<PersistentEntityResource>> personProcessor() {
        return new ResourceProcessor<Resource<PersistentEntityResource>>() {

			public Resource<PersistentEntityResource> process(Resource<PersistentEntityResource> resource) {
				resource.add(new Link(resource.getLink("self").getHref() + "/content", "content"));
                return resource;
			}
        };
    }*/
}
