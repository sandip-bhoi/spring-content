package internal.org.springframework.content.jpa.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCountCallbackHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class BlobResource implements Resource {

    private static Log logger = LogFactory.getLog(BlobResource.class);

    private final String id;
    private final JdbcTemplate template;
    private JdbcTemplateServices templateServices;

    public BlobResource(String id, JdbcTemplate template) {
        this.id = id;
        this.template = template;
        this.templateServices = new JdbcTemplateServicesImpl();
    }

    public BlobResource(String id, JdbcTemplate template, JdbcTemplateServices templateServices) {
        this.id = id;
        this.template = template;
        this.templateServices = templateServices;
    }

    @Override
    public boolean exists() {
        String sql = "SELECT id FROM BLOBS WHERE id='" + this.id + "'";
        RowCountCallbackHandler counter = templateServices.newRowCountCallbackHandler();
        this.template.query(sql, counter);
        return (counter.getRowCount() == 1);
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public URL getURL() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public URI getURI() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getFile() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long contentLength() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long lastModified() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFilename() {
        return this.id;
    }

    @Override
    public String getDescription() {
        return "blob [" + this.id + "]";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStreamCallbackHandler handler = this.templateServices.newInputStreamCallbackHandler("blob");
        String sql = "SELECT blob FROM BLOBS WHERE id='" + this.id + "'";
        this.template.execute(sql, handler);
        return handler.getInputStream();
    }
}
