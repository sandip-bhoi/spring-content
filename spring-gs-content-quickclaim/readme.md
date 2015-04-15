# Spring QuickClaim
Spring boot based application demonstrating spring content mongo and spring content rest.

## Running the application

Mongo is required.  You can get it from [here](www.mongodb.org).  Download, install and start mongo.

To launch the app:-

<pre><code>mvn spring-boot:run</code></pre>

OR

Right-click on the spring-gs-content-quickclaim project in eclipse and select Run As->Sprng Boot Application

Then visit [localhost](http://localhost:8080/)

## Using the app

The application is currently very (overly) simple, just allowing the user to create some metadata, some content and to associate the two.  Specifically in this case the user can create a new claim (the metadata) and upload the claimant's claim form (the content), see this new claim and view the claim form (i.e the two are associated).  

## Understanding the code

Although the app is pretty simple, actually there is quite a lot going on under the covers.  Let's take a look.  

### Spring Data and Metadata

First things first, in order to store the claim (the metadata) we rely on Spring Data (Mongo).  So, in the POM we depend upon:-

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-mongodb</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-rest</artifactId>
		</dependency>

And create a spring boot application:-

	@SpringBootApplication
	public class Application {
		public static void main(String[] args) {
	        SpringApplication.run(Application.class, args);
	    }
	}

As you might expect we then model the Claim object:-

	@Document
	public class Claim {
	
		@Id
		private String claimId;
	
		private String lastName;
		private String firstName;
		
		...
	
and export a "claim" rest API using a ClaimRepository and the @RepositoryRestResource:-

	@RepositoryRestResource(collectionResourceRel = "xclaims", path = "xclaims")
	public interface ClaimRepository extends PagingAndSortingRepository<Claim, String> {
	
	} 

This then allows a client (angular in our case) to create, read, updated and delete Claim objects.  That's it.  Not much code at all.  In fact what we have done here it is not much coding, more delcaring our intent.  It isnt really running code as we haven't written a single line of code that physically does anything with Mongo.  The @SpringBootApplication annotation does all the clever stuff of figuring out we are using mongo db and enabling the application so that this "declared application" translates into an actual, real working application.

### Spring Content and well...content

Spring Content brings the same abstractions to content-handling, giving the developer an equivalent declarative coding style for content, as he has for structured data with Spring Data.

Let's take a look at that next.

In order to store content we need a content store!  In this case we are going to use spring-content-mongo, which uses Mongo DBs GridFS.  We could use any store though; spring-content-jpa, spring-content-s3, spring-content-filesystem, spring-content-maxcdn.  You name it.  If it stores content, it can be used.  

Later on we are also going to export a content handling REST API so we also going to use spring-content-rest.   All up we add the following two additional dependencies to our POM:-

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-content-mongo</artifactId>
			<version>1.2.1.RELEASE</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.content</groupId>
			<artifactId>spring-content-rest</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>

Notice that spring-content-mongo is also "BOOTed"; i.e we are actually depending upon spring-boot-starter-content-mongo, and that this includes similar autoconfiguration capabilities as any other spring boot starter.  (TBD whether we need a spring-boot-starter-content-rest).

Similar to Spring Data, where we modelled our Claim, with Spring Content we also model our content objects.  So in this case we model the claimant's ClaimForm:-

	public class ClaimForm {
	
		@ContentId
		private String contentId;
		
		@ContentLength
		private long contentLength;
		
		@MimeType
		private String mimeType;
		
		...

And also similar to Spring Data, Spring Content has "content" annotations.  These to capture important information about our modelled content objects.  Out of the box spring content supports @ContentId, @ContentLength and @MimeType.  All pretty self explanatory.  The only mandatory annotation is @ContentId.  The others are optional.  Other content stores may also add there own and of course this is just a POJO so if you, as developer, want or need to capture additional information, just add more fields.

Next, we export a content-handling REST API so that clients can manipulate content as easily as they manipulate structured data.  To do this we add a ClaimFormContentStore interface, annotated with the @ContentStoreRestResource, as follows:-

	@ContentStoreRestResource(path="xclaims")
	public interface ClaimFormStore extends ContentStore<ClaimForm, String> {
	
	}   

Just like Spring Data, no code!  Just a declaration of intent to store content.  In real terms an interface that extends a generic ContentStore typed to our content model ClaimForm.  If you investigate the methods on ContentStore you'll see a simple set content handling methods that given a content object S allow us to store and retieve an input stream; i.e. store content!

	public interface ContentStore<S, SID extends Serializable> extends ContentRepository<S, SID> {
		
		void setContent(S property, InputStream content);
		void unsetContent(S property);
		InputStream getContent(S property);
	
	}

Again the @SpringBootApplication is the key here.  During application startup this invokes spring content's autoconfiguration capabilities that detect mongo DB and enable it for content storage by wiring this interface into a real implementation of a content store based on Mongo's GridFS binary store.

### Soring Content REST and associating structured data and content

At this point we can store structured data and we can store content.  This is great but to be really useful we need to associate the content with the structured data.  To do that we need to go back to our modelled Claim object and update it to refer to our modelled ClaimForm object, therefore we modify it as follows:-

	@Document
	public class Claim {
	
		@Id
		private String claimId;
	
		private String lastName;
		private String firstName;
		
		@Content
		private ClaimForm claimForm;
		
		...

All we, as developers, need to do is add a new private member variable called claimForm and, importantly, annotate it as @Content.  Spring Content REST does everything else.  

Because we used the @Content annotation Spring Data REST will treat this property as a special content property.  It will add "claimForm" linkrels to the usual hal+json responses return by Spring Data allowing clients to handle content for this and any other content properties. 

So if we visit the self link of a claim that didn't yet have content we would get this sort of response:-

	{
	  "lastName" : "Warren",
	  "firstName" : "Paul",
	  "_links" : {
	    "self" : {
	      "href" : "http://localhost:8080/xclaims/55143723d4c646850b4bda59"
	    },
	    "claimForm-create" : {
	      "href" : "http://localhost:8080/xclaims/55143723d4c646850b4bda59/claimForm"
	    }
	  }
	}
	 
The claimForm-create linkrel tells clients that they can "follow" this linkrel to add claimForm content.  Practically, they would do this by POSTing the binary content to this URI.

Likewise, if we were to visit the self link of a claim that already has claim content we would see this sort of response:-

	{
	  "lastName" : "Warren",
	  "firstName" : "Paul",
	  "claimForm" : {
	    "contentId" : "b01aa974-8e52-4395-ae95-739aa6b27b11",
	    "contentLength" : 60195,
	    "mimeType" : "application/pdf"
	  },
	  "_links" : {
	    "self" : {
	      "href" : "http://localhost:8080/xclaims/55143723d4c646850b4bda59"
	    },
	    "claimForm" : {
	      "href" : "http://localhost:8080/xclaims/55143723d4c646850b4bda59/claimForm/b01aa974-8e52-4395-ae95-739aa6b27b11"
	    },
	    "claimForm-update" : {
	      "href" : "http://localhost:8080/xclaims/55143723d4c646850b4bda59/claimForm/b01aa974-8e52-4395-ae95-739aa6b27b11"
	    },
	    "claimForm-delete" : {
	      "href" : "http://localhost:8080/xclaims/55143723d4c646850b4bda59/claimForm/b01aa974-8e52-4395-ae95-739aa6b27b11"
	    }
	  }
	} 

This time we see claimForm metadata including the content's length and mime type (recall the @ContentLength and @MimeType annotations) as well as several "claimForm" linkrels.  The first linkrel is the equivalent of a Spring Data's self link.  Following this by performing a GET request will return the response the actual binary with a content-type of application/pdf and a content-length of 60195.  The second linkrel "claimForm-update" supports PUT requests allowing clients to overwrite the existing content with new content and lastly the third linkrel "claimForm-delete" supports DELETE requests allowing the client to remove the content.

## Summary

In summary spring content and spring content rest brings the same programming paradigms to content handling as Spring Data and Spring Data REST brings to structured data handling.

An increasing number of Spring Content implementation projects; spring content JPA, spring content S3 etc are bringing an increasing number of content stores that can be used.  

Given that a large subset of all apps are content-enabled in some way I believe that this delcarative, labour saving style of coding will continue to cement Spring's future as the next generation J2EE. 

Not only that, in my opinion Spring Content represents a common core of content handling upon which the next generation of cloud scale content management systems can be built.   Imagine for a second "Pivotal CMS" built on top of Spring Data Cassandra and Spring Content HDFS.  This would be a CMS that could scale almost infinately and something that, due to the limitations of their legacy technologies, none of the current ECM players; alfresco, opentext, box.com, could match. 
