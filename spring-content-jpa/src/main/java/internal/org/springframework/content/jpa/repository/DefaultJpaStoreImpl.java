package internal.org.springframework.content.jpa.repository;

import java.io.*;

import internal.org.springframework.content.jpa.io.MySQLBlobResource;
import internal.org.springframework.content.jpa.io.BlobResourceFactory;
import internal.org.springframework.content.jpa.io.JdbcTemplateServices;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.repository.ContentStore;

import internal.org.springframework.content.jpa.operations.JpaContentTemplate;
import org.springframework.content.commons.utils.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;

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
        MySQLBlobResource resource = blobResourceFactory.newBlobResource(id.toString());
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            logger.error(String.format("Unable to get input stream for resource %s", id));
        }
        return null;
    }

	@Override
	public void setContent(S metadata, InputStream content) {
        Object id = BeanUtils.getFieldWithAnnotation(metadata, ContentId.class);
        MySQLBlobResource resource = blobResourceFactory.newBlobResource(id.toString());
        OutputStream os = null;
        try {
            os = resource.getOutputStream();
            IOUtils.copyLarge(content, os);
        } catch (IOException e) {
            logger.error(String.format("Unable to get input stream for resource %s", id));
        } finally {
            IOUtils.closeQuietly(content);
            IOUtils.closeQuietly(os);
        }

        return;
	}

	@Override
	public void unsetContent(S metadata) {
//		this.template.unsetContent(metadata);
	}
}
