package internal.org.springframework.content.jpa.repository;

import java.io.*;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import internal.org.springframework.content.jpa.io.BlobResource;
import internal.org.springframework.content.jpa.io.BlobResourceFactory;
import internal.org.springframework.content.jpa.io.JdbcTemplateServices;
import internal.org.springframework.content.jpa.io.JdbcTemplateServicesImpl;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.io.FileRemover;
import org.springframework.content.commons.io.ObservableInputStream;
import org.springframework.content.commons.repository.ContentStore;

import internal.org.springframework.content.jpa.operations.JpaContentTemplate;
import org.springframework.content.commons.utils.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import javax.sql.DataSource;

public class DefaultJpaStoreImpl<S, SID extends Serializable> implements ContentStore<S,SID> {

    private static Log logger = LogFactory.getLog(BlobResourceFactory.class);

    private JdbcTemplate template;
    private JdbcTemplateServices templateServices;
    private BlobResourceFactory blobResourceFactory;

    public DefaultJpaStoreImpl(JpaContentTemplate template) {
//		this.template = template;
	}

	public DefaultJpaStoreImpl(BlobResourceFactory blobResourceFactory) {
        this.blobResourceFactory = blobResourceFactory;
	}

    @Override
	public InputStream getContent(S metadata) {
        Object id = BeanUtils.getFieldWithAnnotation(metadata, ContentId.class);
        BlobResource resource = blobResourceFactory.newBlobResource(id.toString());
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            logger.error(String.format("Unable to get input stream for resource %s", id));
        }
        return null;
    }

	@Override
	public void setContent(S metadata, InputStream content) {
//        Object id = BeanUtils.getFieldWithAnnotation(metadata, ContentId.class);
//        BlobResource resource = blobResourceFactory.newBlobResource(id.toString());
//        try {
//            OutputStream os = resource.getOutputStream();
//        } catch (IOException e) {
//            logger.error(String.format("Unable to get input stream for resource %s", id));
//        }
        return;
	}

	@Override
	public void unsetContent(S metadata) {
//		this.template.unsetContent(metadata);
	}
}
