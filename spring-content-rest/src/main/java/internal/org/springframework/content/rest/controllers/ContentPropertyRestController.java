package internal.org.springframework.content.rest.controllers;

import internal.org.springframework.content.rest.annotations.ContentRestController;
import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;
import internal.org.springframework.content.rest.links.ContentResource;
import internal.org.springframework.content.rest.utils.ContentStoreUtils;
import internal.org.springframework.content.rest.utils.PersistentEntityUtils;
import internal.org.springframework.content.rest.utils.RepositoryUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@ContentRestController
public class ContentPropertyRestController {

	private static final String BASE_MAPPING = "/{repository}/{id}/{contentProperty}/{contentId}";

	@Autowired
	Repositories repositories;
	
	@Autowired
	RepositoryInvokerFactory repositoryInvokerFactory;
	
	@Autowired
	ResourceMappings repositoryMappings;
	
	@Autowired 
	ContentStoreService storeService;
	
	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getContent(@PathVariable String repository, 
														  @PathVariable String id, 
														  @PathVariable String contentProperty,
														  @PathVariable String contentId) 
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
			throw new ResourceNotFoundException();

		// multi-valued porperty?
		if (PersistentEntityUtils.isPropertyMultiValued(prop)) {

			// instantiate an instance of the member type and add it
			if (prop.isArray()) {
				throw new UnsupportedOperationException();
			} else if (prop.isCollectionLike()) {
				propVal = findContentObject(contentId, (List)propVal);
				if (propVal == null)
					throw new ResourceNotFoundException();
			}
		}
		
		final HttpHeaders headers = new HttpHeaders();
		if (BeanUtils.hasFieldWithAnnotation(propVal, MimeType.class))
			headers.add("Content-Type", BeanUtils.getFieldWithAnnotation(propVal, MimeType.class).toString());
		if (BeanUtils.hasFieldWithAnnotation(propVal, ContentLength.class))
			headers.add("Content-Length", BeanUtils.getFieldWithAnnotation(propVal, ContentLength.class).toString());
		
		InputStreamResource inputStreamResource = new InputStreamResource(info.getImpementation().getContent(propVal));
		//httpHeaders.setContentLength(contentLengthOfStream);
		return new ResponseEntity<InputStreamResource>(inputStreamResource, headers, HttpStatus.OK);
	}

	/**
	 * Handles all POSTed requests that aren't multipart forms
	 * 
	 * This method is also called by modern browsers and IE >= 10
	 * @throws IOException 
	 * @throws HttpRequestMethodNotSupportedException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.POST, headers = "content-type!=multipart/form-data")
	@ResponseBody
	public void postContent(final HttpServletRequest request,
	        				  final HttpServletResponse response,
	        				  @PathVariable String repository, 
							  @PathVariable String id, 
							  @PathVariable String contentProperty,
							  @PathVariable String contentId) 
									  throws IOException, HttpRequestMethodNotSupportedException, InstantiationException, IllegalAccessException {
		
		this.replaceContentInternal(repository, id, contentProperty, contentId, request.getHeader("Content-Type"), request.getInputStream());
	}	
	

	@RequestMapping(value = BASE_MAPPING, method = RequestMethod.POST, headers = "content-type=multipart/form-data")
	@ResponseBody
	public void postMultipartContent(@PathVariable String repository, 
							  @PathVariable String id, 
							  @PathVariable String contentProperty,
							  @PathVariable String contentId,
							  @RequestParam("file") MultipartFile multiPart)
									  throws IOException, HttpRequestMethodNotSupportedException, InstantiationException, IllegalAccessException {

		this.replaceContentInternal(repository, id, contentProperty, contentId, multiPart.getContentType(), multiPart.getInputStream());
	}

	private void replaceContentInternal(String repository,
									 String id, 
									 String contentProperty, 
									 String contentId, 
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
		if (propVal == null) 
			throw new ResourceNotFoundException();

		// multi-valued porperty?
		if (PersistentEntityUtils.isPropertyMultiValued(prop)) {

			if (prop.isArray()) {
				throw new UnsupportedOperationException();
			} else if (prop.isCollectionLike()) {
				propVal = findContentObject(contentId, (List)propVal);
				if (propVal == null)
					throw new ResourceNotFoundException();
			}
		}

		if (BeanUtils.hasFieldWithAnnotation(propVal, MimeType.class)) {
			BeanUtils.setFieldWithAnnotation(propVal, MimeType.class, mimeType);
		}
		
		info.getImpementation().setContent(propVal, stream);
		
		repositoryInvokerFactory.getInvokerFor(domainType).invokeSave(domainObj);
	}

	private Object findContentObject(String id, List contents) {
		for (Object content : contents) {
			if (BeanUtils.hasFieldWithAnnotation(content, ContentId.class) && BeanUtils.getFieldWithAnnotation(content, ContentId.class) != null) {
				String candidateId = BeanUtils.getFieldWithAnnotation(content, ContentId.class).toString();
				if (candidateId.equals(id))
					return content;
			}
		}
		return null;
	}

}
