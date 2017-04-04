package internal.org.springframework.content.fs.repository;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.JustBeforeEach;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.placementstrategy.PlacementService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;

import internal.org.springframework.content.fs.operations.FileResourceTemplate;

@RunWith(Ginkgo4jRunner.class)
public class DefaultFilesystemContentRepositoryImplTest {
    private DefaultFileSystemContentRepositoryImpl<TestEntity, String> filesystemContentRepoImpl;
    private FileResourceTemplate fileResourceTemplate;
    private FileSystemResource root;
    private PlacementService placement;
    private TestEntity entity;
    
    private WritableResource resource;
    private Resource nonExistentResource;

    private InputStream content;
    private OutputStream output;

    private InputStream result;

    {
        Describe("DefaultFilesystemContentRepositoryImpl", () -> {
            BeforeEach(() -> {
                root = mock(FileSystemResource.class);
                resource = mock(WritableResource.class);
                fileResourceTemplate = mock(FileResourceTemplate.class);
                placement = mock(PlacementService.class);
                filesystemContentRepoImpl = new DefaultFileSystemContentRepositoryImpl<TestEntity, String>(root, placement);
                
            });
            Context("#setContent", () -> {
                BeforeEach(() -> {
                    entity = new TestEntity();
                    content = new ByteArrayInputStream("Hello content world!".getBytes());

                    when(placement.getLocation(anyObject())).thenReturn("/some/deep/location");

                    when(root.createRelative(eq("/some/deep/location"))).thenReturn(resource);
//                    when(fileResourceTemplate.get(anyObject())).thenReturn(resource);
                    output = mock(OutputStream.class);
                    when(resource.getOutputStream()).thenReturn(output);

                    when(resource.contentLength()).thenReturn(20L);
                });

                JustBeforeEach(() -> {
                    filesystemContentRepoImpl.setContent(entity, content);
                });

                It("should get a location from the placement service and use that to create the resource", () -> {
                	verify(placement).getLocation(anyObject());
                	verify(root).createRelative(eq("/some/deep/location"));
                });
                
                It("should change the content length", () -> {
                    assertThat(entity.getContentLen(), is(20L));
                });

                Context("#when the content already exists", () -> {
                    BeforeEach(() -> {
                        entity.setContentId("abcd");
                    });

                    It("should write to the resource's outputstream", () -> {
                        verify(resource).getOutputStream();
                        verify(output, times(1)).write(Matchers.<byte[]>any(), eq(0), eq(20));
                    });
                });

                Context("when the content does not already exist", () -> {
                    BeforeEach(() -> {
                        assertThat(entity.getContentId(), is(nullValue()));
                    });
                    It("should make a new UUID", () -> {
                        assertThat(entity.getContentId(), is(not(nullValue())));
                    });
                    It("should create a new resource", () -> {
                    	verify(root).createRelative(eq("/some/deep/location"));
                    });
                    It("should write to the resource's outputstream", () -> {
                        verify(resource).getOutputStream();
                        verify(output, times(1)).write(Matchers.<byte[]>any(), eq(0), eq(20));
                    });
                });
            });

            Context("#getContent", () -> {
                BeforeEach(() -> {
                    entity = new TestEntity();
                    content = mock(InputStream.class);
                    entity.setContentId("abcd-efgh");
                  
                    when(placement.getLocation(eq("abcd-efgh"))).thenReturn("/abcd/efgh");
                    
//                    when(fileResourceTemplate.get(anyObject())).thenReturn(resource);
                    when(root.createRelative(eq("/abcd/efgh"))).thenReturn(resource);
                    when(resource.getInputStream()).thenReturn(content);
                });

                JustBeforeEach(() -> {
                	result = filesystemContentRepoImpl.getContent(entity);
                });
                
                Context("when the resource exists", () -> {
                    BeforeEach(() -> {
                        when(resource.exists()).thenReturn(true);
                    });

                    It("should get content", () -> {
                        assertThat(result, is(content));
                    });
                });
                Context("when the resource does not exists", () -> {
                    BeforeEach(() -> {
                		nonExistentResource = mock(Resource.class);
                		when(resource.exists()).thenReturn(true);

                		when(root.createRelative(eq("/abcd/efgh"))).thenReturn(nonExistentResource);
                        when(root.createRelative(eq("abcd-efgh"))).thenReturn(nonExistentResource);
                    });

                    It("should not find the content", () -> {
                        assertThat(result, is(nullValue()));
                    });
                });
                Context("when the resource exists in the old location", () -> {
                	BeforeEach(() -> {
                		nonExistentResource = mock(Resource.class);
                        when(root.createRelative(eq("/abcd/efgh"))).thenReturn(nonExistentResource);
                        when(nonExistentResource.exists()).thenReturn(false);

                        when(root.createRelative(eq("abcd-efgh"))).thenReturn(resource);
                        when(resource.exists()).thenReturn(true);
                	});
                	It("should check the new location and then the old", () -> {
                		InOrder inOrder = Mockito.inOrder(root);
                		
                		inOrder.verify(root).createRelative(eq("/abcd/efgh"));
                		inOrder.verify(root).createRelative(eq("abcd-efgh"));
                		inOrder.verifyNoMoreInteractions();
                	});
                    It("should get content", () -> {
                        assertThat(result, is(content));
                    });
                });
            });

            Context("#unsetContent", () -> {
                BeforeEach(() -> {
                    entity = new TestEntity();
                    entity.setContentId("abcd-efgh");
                    entity.setContentLen(100L);
                });

                JustBeforeEach(() -> {
                	filesystemContentRepoImpl.unsetContent(entity);
                });

                Context("when the content exists in the new location", () -> {
                	BeforeEach(() -> {
                		when(placement.getLocation("abcd-efgh")).thenReturn("/abcd/efgh");
                		
	            		when(root.createRelative("/abcd/efgh")).thenReturn(resource);
	            		when(resource.exists()).thenReturn(true);
                	});
                	It("should unset content", () -> {
                		verify(fileResourceTemplate).delete(eq(resource));
                		assertThat(entity.getContentId(), is(nullValue()));
                		assertThat(entity.getContentLen(), is(0L));
                	});
                });
                
                Context("when the content exists in the old location", () -> {
                	BeforeEach(() -> {
                        when(placement.getLocation("abcd-efgh")).thenReturn("/abcd/efgh");

                        nonExistentResource = mock(Resource.class);
                        when(fileResourceTemplate.get(eq("/abcd/efgh"))).thenReturn(nonExistentResource);
                        when(nonExistentResource.exists()).thenReturn(false);

                        when(fileResourceTemplate.get(eq("abcd-efgh"))).thenReturn(resource);
                        when(resource.exists()).thenReturn(true);
                        
                        when(fileResourceTemplate.getLocation("abcd-efgh")).thenReturn("/some-root/abcd-efgh");
                	});
                	It("should unset the content", () -> {
                		verify(fileResourceTemplate).delete(resource);
                		assertThat(entity.getContentId(), is(nullValue()));
                		assertThat(entity.getContentLen(), is(0L));
                	});
                });
                
                Context("when the content doesnt exist", () -> {
                	BeforeEach(() -> {
                        when(placement.getLocation("abcd-efgh")).thenReturn("/abcd/efgh");

                		nonExistentResource = mock(Resource.class);
                        when(fileResourceTemplate.get(eq("/abcd/efgh"))).thenReturn(nonExistentResource);
                        when(nonExistentResource.exists()).thenReturn(false);

                		nonExistentResource = mock(Resource.class);
                        when(fileResourceTemplate.get(eq("abcd-efgh"))).thenReturn(nonExistentResource);
                        when(nonExistentResource.exists()).thenReturn(false);
                	});
                	It("should unset the content", () -> {
                		verify(fileResourceTemplate, never()).delete(nonExistentResource);
                		assertThat(entity.getContentId(), is(nullValue()));
                		assertThat(entity.getContentLen(), is(0L));
                	});
                });
            });
        });
    }

    public static class TestEntity {
        @ContentId
        private String contentId;

        @ContentLength
        private long contentLen;

        public TestEntity() {
            this.contentId = null;
        }

        public TestEntity(String contentId) {
            this.contentId = new String(contentId);
        }

        public String getContentId() {
            return this.contentId;
        }

        public void setContentId(String contentId) {
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
