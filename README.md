# Spring Content (incl. Spring Content Rest)#

(Hopefully) a new spring community project adding basic content handling patterns to Spring for both java and REST.  This is done through a central "content store" abstraction with basic content store implementations for JPA and mongo databases and one or more content delivery networks.

## Projects

- spring-content-commons; common core
- spring-content-mongo; mongo implementation of a ContentStore that stores content in GridFs
- spring-content-rest; a REST layer on to of spring content that adds content links to spring data rest

- spring-boot-starter-content-mongo; spring boot starter including autoconfiguration for spring-content-mongo
- spring-boot-starter-content-rest; spring boot starter incl. autoconfiguration for spring-content-rest

### Example/Test Projects
- spring-eg-content-mongo; example of spring-content-mongo in use
- spring-eg-starter-content-mongo; example of spring-content-mongo in use in a spring boot app
- spring-eg-content-rest; example of spring-content-rest in use (on top of a mongo content store)

### Getting Started
- spring-quickclaim; example spring-boot based application based on an insurance companies claim system showing spring content and spring content REST in action
