package org.springframework.content.io;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.JustBeforeEach;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.runner.RunWith;
import org.springframework.core.io.Resource;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jRunner;

@RunWith(Ginkgo4jRunner.class)
public class DeletableResourceTest {

	private FileSystemResourceLoader loader = null;
	
	private String location;
	
	private File parent;
	private File file;
	
	{
		Describe("DeletableResource", () -> {
			Context("#delete", () -> {
				BeforeEach(() -> {
					parent = new File(System.getProperty("java.io.tmpdir") + UUID.randomUUID());
				});
				JustBeforeEach(() -> {
					loader = new FileSystemResourceLoader(parent.getPath() + "/");
					Resource resource = loader.getResource(location);
					assertThat(resource, instanceOf(DeletableResource.class));
					((DeletableResource)resource).delete();
				});
				Context("given a file resource that exists", () -> {
					BeforeEach(() -> {
  						location = "FileSystemResourceLoaderTest.tmp";
  						file = new File(parent, location);
 						FileUtils.touch(file);
 						assertThat(file.exists(), is(true));
					});
					It("should delete the underlying file", () -> {
						assertThat(file.exists(), is(false));
					});
				});
			});
		});
	}
	
}
