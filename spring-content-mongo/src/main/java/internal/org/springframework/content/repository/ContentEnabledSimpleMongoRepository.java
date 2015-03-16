package internal.org.springframework.content.repository;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.UUID;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.content.common.repository.ContentStore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

public class ContentEnabledSimpleMongoRepository<T, ID extends Serializable, S, SID extends Serializable>
				extends SimpleMongoRepository<T,ID>
				implements ContentStore<S,SID> {

	private GridFsOperations gridOps;

	public ContentEnabledSimpleMongoRepository(MongoEntityInformation metadata, MongoOperations mongoOperations, GridFsOperations gridOps) {
		super(metadata, mongoOperations);
		this.gridOps = gridOps;
	}

	@Override
	public void setContent(S property, InputStream content) {
		String contentId = findContentId(property);
		if (contentId == null) {
			contentId = UUID.randomUUID().toString();
			setContentId(property, contentId);
		}
		gridOps.store(content, contentId);
	}

	@Override
	public InputStream getContent(S property) {
		if (property == null)
			return null;
		String contentId = findContentId(property);
		if (contentId == null)
			return null;
		GridFsResource resource = gridOps.getResource(contentId);
		if (resource != null)
			try {
				return resource.getInputStream();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return null;
	}

	String findContentId(S property) throws SecurityException, BeansException {
		String contentId = null;
		BeanWrapper wrapper = new BeanWrapperImpl(property);
		PropertyDescriptor[] descriptors = wrapper.getPropertyDescriptors();
		for (PropertyDescriptor descriptor : descriptors) {
			String prop = descriptor.getName();
			Field theField = null;
			try {
				theField = property.getClass().getDeclaredField(prop);
				if (theField != null) {
					if (theField.getAnnotation(Id.class) != null) {
						Object val = wrapper.getPropertyValue(prop);
						if (val != null)
							contentId = val.toString();
					}
				}
			} catch (NoSuchFieldException ex) {
				continue;
			}
		}
		return contentId;
	}

	private void setContentId(S property, String contentId) {
		BeanWrapper wrapper = new BeanWrapperImpl(property);
		PropertyDescriptor[] descriptors = wrapper.getPropertyDescriptors();
		for (PropertyDescriptor descriptor : descriptors) {
			String prop = descriptor.getName();
			Field theField = null;
			try {
				theField = property.getClass().getDeclaredField(prop);
				if (theField != null) {
					if (theField.getAnnotation(Id.class) != null) {
						wrapper.setPropertyValue(prop, contentId);
					}
				}
			} catch (NoSuchFieldException ex) {
				continue;
			}
		}
	}
}
