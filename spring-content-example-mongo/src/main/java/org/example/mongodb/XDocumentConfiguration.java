package org.example.mongodb;

import org.springframework.context.annotation.Configuration;

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
