package org.example.mongodb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.example.mongodb.document.XContentContentStore;
import org.example.mongodb.document.XContent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StreamUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { XDocumentTestConfig.class })
public class XDocumentContentStoreTests {

	@Autowired
	private XContentContentStore repo;
	
	@Test
	public void test() throws IOException {
		XContent content = new XContent();
		String setContent = "some content";
		repo.setContent(content, new ByteArrayInputStream(setContent.getBytes(Charset.defaultCharset())));

		InputStream in = repo.getContent(content);
		String getContent = StreamUtils.copyToString(in, Charset.defaultCharset());
		
		org.junit.Assert.assertEquals(setContent, getContent);
	}
}
