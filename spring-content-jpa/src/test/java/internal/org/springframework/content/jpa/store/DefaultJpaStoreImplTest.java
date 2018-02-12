package internal.org.springframework.content.jpa.store;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;
import internal.org.springframework.content.jpa.io.MySQLBlobResource;
import internal.org.springframework.content.jpa.io.BlobResourceFactory;
import internal.org.springframework.content.jpa.repository.DefaultJpaStoreImpl;
import org.junit.runner.RunWith;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Random;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(Ginkgo4jRunner.class)
public class DefaultJpaStoreImplTest {

    private DefaultJpaStoreImpl<Object,String> store;

    // actors
    private TestEntity entity;
    private InputStream stream;

    // mocks
    private DataSource datasource;
    private Connection connection;
    private DatabaseMetaData metadata;
    private ResultSet resultSet;
    private PreparedStatement statement;
    private InputStream inputStream;

    private Blob blob;

    private BlobResourceFactory blobResourceFactory;
    private MySQLBlobResource resource;

    {
        Describe("DefaultJpaStoreImpl", () -> {
            Context("#getContent", () -> {
                BeforeEach(() -> {
                    blobResourceFactory = mock(BlobResourceFactory.class);
                    resource = mock(MySQLBlobResource.class);

                    entity = new TestEntity(12345);

                    when(blobResourceFactory.newBlobResource(entity.getContentId().toString())).thenReturn(resource);
                });
                JustBeforeEach(() -> {
                    store = new DefaultJpaStoreImpl(blobResourceFactory);
                    inputStream = store.getContent(entity);
                });
                Context("given content", () -> {
                    BeforeEach(() -> {
                        stream = new ByteArrayInputStream("hello content world!".getBytes());

                        when(resource.getInputStream()).thenReturn(stream);
                    });

                    It("should use the blob resource factory to create a new blob resource", () -> {
                        verify(blobResourceFactory).newBlobResource(entity.getContentId().toString());
                    });

                    It("should return an inputstream", () -> {
                        assertThat(inputStream, is(not(nullValue())));
                    });
                });
                Context("given fetching the input stream fails", () -> {
                    BeforeEach(() -> {
                        when(resource.getInputStream()).thenThrow(new IOException());
                    });
                    It("should return null", () -> {
                        assertThat(inputStream, is(nullValue()));
                    });
                });
            });
            Context("#setContent", () -> {
                BeforeEach(() -> {
                    blobResourceFactory = mock(BlobResourceFactory.class);
                    resource = mock(MySQLBlobResource.class);

                    entity = new TestEntity(12345);
                    byte[] content = new byte[5000];
                    new Random().nextBytes(content);
                    inputStream = new ByteArrayInputStream(content);

                    when(blobResourceFactory.newBlobResource(entity.getContentId().toString())).thenReturn(resource);
                });
                JustBeforeEach(() -> {
                    store = new DefaultJpaStoreImpl(blobResourceFactory);
                    store.setContent(entity, inputStream);
                });
                Context("when the row does not exist", () -> {
                    BeforeEach(() -> {

                    });
                    It("should use the blob resource factory to create a blob resource for the entity", () -> {
                        verify(blobResourceFactory).newBlobResource(entity.getContentId().toString());

                    });
                });
            });
        });
    }

    public static class TestEntity {
        @ContentId
        private Integer contentId;
        @ContentLength
        private long contentLen;

        public TestEntity() {
            this.contentId = null;
        }

        public TestEntity(int contentId) {
            this.contentId = new Integer(contentId);
        }

        public Integer getContentId() {
            return this.contentId;
        }

        public void setContentId(Integer contentId) {
            this.contentId = contentId;
        }

        public long getContentLen() {
            return contentLen;
        }

        public void setContentLen(long contentLen) {
            this.contentLen = contentLen;
        }

    }

}
