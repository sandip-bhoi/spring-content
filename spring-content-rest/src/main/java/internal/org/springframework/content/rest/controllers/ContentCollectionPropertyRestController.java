package internal.org.springframework.content.rest.controllers;

import internal.org.springframework.content.rest.annotations.ContentRestController;
import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;
import internal.org.springframework.content.rest.links.ContentLinks;
import internal.org.springframework.content.rest.links.ContentResource;
import internal.org.springframework.content.rest.utils.ContentStoreUtils;
import internal.org.springframework.content.rest.utils.PersistentEntityUtils;
import internal.org.springframework.content.rest.utils.RepositoryUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.annotations.ContentId;
import org.springframework.content.annotations.ContentLength;
import org.springframework.content.annotations.MimeType;
import org.springframework.content.common.storeservice.ContentStoreInfo;
import org.springframework.content.common.storeservice.ContentStoreService;
import org.springframework.content.common.utils.BeanUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.invoke.RepositoryInvoker;
import org.springframework.data.rest.core.invoke.RepositoryInvokerFactory;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.webmvc.BaseUri;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;

@ContentRestController
public class ContentCollectionPropertyRestController {

	private static final String BASE_MAPPING = "/{repository}/{id}/{contentProperty}";

	@Autowired
	Repositories repositories;
	
	@Autowired
	RepositoryInvokerFactory repositoryInvokerFactory;
	
	@Autowired
	ResourceMappings repositoryMappings;
	
	@Autowired 
	ContentStoreService storeService;
	
	@Autowired
	ListableBeanFactory beanFactory;
	
	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET)
	public ResponseEntity<Resources<?>> getCollection(final HttpServletRequest request,
													  @PathVariable String repository, 
												  	  @PathVariable String id, 
												  	  @PathVariable String contentProperty) 
			throws HttpRequestMethodNotSupportedException {
		
		ResourceMetadata mapping = RepositoryUtils.findRepositoryMapping(repositories, repositoryMappings, repository);
		Class<?> domainType = mapping.getDomainType();

		Object domainObj = RepositoryUtils.findDomainObject(repositoryInvokerFactory, domainType, id);
				
		ContentStoreInfo info = ContentStoreUtils.findContentStore(storeService, repository);

		PersistentEntity<?,?> entity = PersistentEntityUtils.findPersistentEntity(repositories, domainType);
		if (null == entity)
			throw new ResourceNotFoundException();
		PersistentProperty<?> prop = entity.getPersistentProperty(contentProperty);
		if (null == prop)
			throw new ResourceNotFoundException();

		BeanWrapper<Object> wrapper = BeanWrapper.create(domainObj, null);
		Object propVal = wrapper.getProperty(prop);
		if (propVal == null)
			throw new ResourceNotFoundException("No content");

		if (prop.isArray() || prop.isCollectionLike()) {
			List<Resource<?>> resources = toResources(new ContentLinks(request.getRequestURI()), (List)propVal);
			return new ResponseEntity<Resources<?>>(new Resources(resources), HttpStatus.OK);
		}
		
		return null;
	}

	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.POST, headers = "content-type!=multipart/form-data")
	@ResponseBody
	public ResponseEntity<Resource<?>> postContent(final HttpServletRequest request,
	        				final HttpServletResponse response,
	        				@PathVariable String repository, 
							@PathVariable String id, 
							@PathVariable String contentProperty) 
									throws IOException, HttpRequestMethodNotSupportedException, InstantiationException, IllegalAccessException {
		
		Object newContent = this.saveContentInternal(repository, id, contentProperty, request.getHeader("Content-Type"), request.getInputStream());
		if (newContent != null) {
			Resource<?> contentResource = toResource(request, newContent);
			return new ResponseEntity<Resource<?>>(contentResource, HttpStatus.CREATED);
		}
		return null;
	}

	Resource<?> toResource(final HttpServletRequest request, Object newContent)
			throws SecurityException, BeansException {
		Link self = new Link(StringUtils.trimTrailingCharacter(request.getRequestURL().toString(), '/') + "/" + BeanUtils.getFieldWithAnnotation(newContent, ContentId.class));
		Resource<?> contentResource = new Resource(newContent, Collections.singletonList(self));
		return contentResource;
	}	

	private List<Resource<?>> toResources(ContentLinks cl, List contents) {
		List<Resource<?>> resources = new ArrayList<Resource<?>>();
		for (Object content : contents) {
			if (BeanUtils.hasFieldWithAnnotation(content, ContentId.class) && BeanUtils.getFieldWithAnnotation(content, ContentId.class) != null) {
				ContentResource resource = new ContentResource(content, cl.linkToContent(content));
				resources.add(resource);
			}
		}
		return resources;
	}

	private List<Link> links(ContentLinks cl, Object... contents) {
		List<Link> links = new ArrayList<Link>();
		for (Object content : contents) {
			if (BeanUtils.hasFieldWithAnnotation(content, ContentId.class) && BeanUtils.getFieldWithAnnotation(content, ContentId.class) != null)
				links.add(cl.linkToContent(content));
		}
		return links;
	}

	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.POST, headers = "content-type=multipart/form-data")
	@ResponseBody
	public void postMultipartContent(@PathVariable String repository, 
							  @PathVariable String id, 
							  @PathVariable String contentProperty,
							  @RequestParam("file") MultipartFile multiPart)
									  throws IOException, HttpRequestMethodNotSupportedException, InstantiationException, IllegalAccessException {

		//this.saveContentInternal(repository, id, contentProperty, contentId, multiPart.getContentType(), multiPart.getInputStream());
	}

	private Object saveContentInternal(String repository,
									 String id, 
									 String contentProperty,  
									 String mimeType,
									 InputStream stream) 
			throws HttpRequestMethodNotSupportedException {
		
		ResourceMetadata mapping = RepositoryUtils.findRepositoryMapping(repositories, repositoryMappings, repository);
		Class<?> domainType = mapping.getDomainType();

		Object domainObj = RepositoryUtils.findDomainObject(repositoryInvokerFactory, domainType, id);
				
		ContentStoreInfo info = ContentStoreUtils.findContentStore(storeService, repository);

		PersistentEntity<?,?> entity = PersistentEntityUtils.findPersistentEntity(repositories, domainType);
		if (null == entity)
			throw new ResourceNotFoundException();
		PersistentProperty<?> prop = entity.getPersistentProperty(contentProperty);
		if (null == prop)
			throw new ResourceNotFoundException();

		BeanWrapper<Object> wrapper = BeanWrapper.create(domainObj, null);
		Object propVal = wrapper.getProperty(prop);
		
		// null single-valued content property
		if (propVal == null && !PersistentEntityUtils.isPropertyMultiValued(prop)) {
			propVal = instantiate(info.getDomainObjectClass());
			wrapper.setProperty(prop, propVal);
		} 
		// null multi-valued content property
		else if (propVal == null && PersistentEntityUtils.isPropertyMultiValued(prop)) {
			// TODO: instantiate an instance of the required arrays or collection/set/list and then 
			// an instance of the content property and add it to the list
		} 
		// non-null multi-valued porperty
		else if (propVal != null && PersistentEntityUtils.isPropertyMultiValued(prop)) {

			// instantiate an instance of the member type and add it
			if (prop.isArray()) {
				Class memberType = propVal.getClass().getComponentType();
				Object member = instantiate(memberType);
				Object newArray = Array.newInstance(propVal.getClass(), Array.getLength(propVal) + 1);
				System.arraycopy(propVal, 0, newArray, 0, Array.getLength(propVal));
				Array.set(newArray, Array.getLength(propVal), member);
				wrapper.setProperty(prop, newArray);
				propVal = member;
				
			} else if (prop.isCollectionLike()) {
				Class<?> memberType = prop.getActualType();
				Object member = instantiate(memberType);
				((Collection)propVal).add(member);
				propVal = member;
			}
		}

		if (BeanUtils.hasFieldWithAnnotation(propVal, MimeType.class)) {
			BeanUtils.setFieldWithAnnotation(propVal, MimeType.class, mimeType);
		}
		
		info.getImpementation().setContent(propVal, stream);
		
		repositoryInvokerFactory.getInvokerFor(domainType).invokeSave(domainObj);
		
		return propVal;
	}
	
	private Object instantiate(Class<?> clazz) {
		Object newObject = null;
		try {
			newObject = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return newObject;
	}
}
