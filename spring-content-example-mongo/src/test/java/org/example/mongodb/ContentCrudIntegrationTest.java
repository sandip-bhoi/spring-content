package org.example.mongodb;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

import java.io.ByteArrayInputStream;

import org.apache.http.HttpStatus;
import org.example.mongodb.document.XContent;
import org.example.mongodb.document.XContentContentStore;
import org.example.mongodb.document.XDocument;
import org.example.mongodb.document.XDocumentRepository;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = org.example.mongodb.Application.class)   
@WebAppConfiguration   
@IntegrationTest("server.port:0")  
public class ContentCrudIntegrationTest {

	@Autowired
	private XDocumentRepository docRepo;
	
	@Autowired
	private XContentContentStore contentStore;
	
    @Value("${local.server.port}")   // 6
    int port;

    private XDocument canSetDoc;
    private XDocument canGetDoc;
    
    @Before
    public void setUp() throws Exception {
    	
        RestAssured.port = port;
    	
    	// ensure clean state
    	docRepo.deleteAll();

    	// create a doc that can set content on
    	canSetDoc = new XDocument();
    	canSetDoc.setTitle("Document without content");
    	docRepo.save(canSetDoc);

    	// create a doc that can get content from
    	canGetDoc = new XDocument();
    	canGetDoc.setTitle("Document with content");
    	docRepo.save(canGetDoc);
    	canGetDoc.setContent(new XContent());
    	canGetDoc.getContent().setMimeType("plain/text");
    	contentStore.setContent(canGetDoc.getContent(), new ByteArrayInputStream("This is plain text content!".getBytes()));
    	docRepo.save(canGetDoc);
    }

    @Test
    public void canSetContent() {
    	JsonPath response = 
    	given()
			.contentType("plain/text")
			.content("This is a plain text book!".getBytes())
	    .when()
	        .post("/documents/" + canSetDoc.getId() + "/content")
	    .then()
	    	.statusCode(HttpStatus.SC_CREATED)
	    	.extract()
	    		.jsonPath();
    	
    	Assert.assertNotNull(response.get("links.find { link -> link.rel == 'self'}"));
    	Assert.assertNotNull(response.get("links.find { link -> link.rel == 'self'}.href"));
    }

    @Test
    public void canGetContent() {
    	JsonPath response = 
		    when()
		        .get("/documents/" + canGetDoc.getId())
		    .then()
		    	.statusCode(HttpStatus.SC_OK)
		    	.extract()
		    		.jsonPath();
    	
    	Assert.assertNotNull(response.get("_links.content"));
    	Assert.assertNotNull(response.get("_links.content.href"));

    	String contentUrl = response.get("_links.content.href");
    	when()
    		.get(contentUrl)
    	.then()
    		.assertThat()
    			.contentType(Matchers.startsWith("plain/text"))
    			.body(Matchers.equalTo("This is plain text content!"));
    }
}
