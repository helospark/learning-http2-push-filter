# Learning HTTP/2.0 server push filter

Filter for Servlet API 4.0 that learnes which secondary resources are associated with the primary resources and perform HTTP/2.0 server push with these resources.

## Introduction

HTTP/2.0 introduced a very exiting feature called server push. In a nutshell this means that if a client asks for a resource the server may respond
 with additional resources (besides the original resource) as well. 
This is very useful in a modern website, for example when the client asks for `index.html` file, the server can know that to render that file
 the client will also need `style.css`, `script.js` and `icon.jpg` files as well. Instead of waiting for all of these requests separately and
 every time suffer the cost of network latency, the server can just send back all of these resources at once.

![Push working](https://image.slidesharecdn.com/http2-150403150729-conversion-gate01/95/http2-and-java-current-status-15-638.jpg?cb=1438699776)

Unfortunatelly manually configuring the associated resources for every resource is a very laborious work, that's why this project was born.

In the following text I will call the initial resource (usually html, jsp, etc.) as primary resource and the associated resources (like jpg, js, css, etc.)
 as secondary resource.

This project is a Servlet filter that captures every request and automatically learns to associate primary resources with secondary resources
 and automatically pushing those resources back to the browser.

It does this by using the `referer` header browser sets, which indicates who refered that resource. The basic idea is if Resource2 is loaded with a refer
 header to Resource1, than a connection is drawn between these and next time Resource1 is requested Resource2 is also pushed. 
In reality this filter has a bit more control (TBD):

   - evicting old resources and learning resource change
   - blacklisting and whitelisting browsers/resources
   - set how many calls are needed to make association between primary and secondary resources


## Usage

Compilation with Maven:

       mvn clean install

You will need to add it as a Maven dependency (Gradle could also be used):
Note this dependency is (not yet) in Maven central repository:

         <dependency>
               <groupId>com.helospark</groupId>
               <artifactId>learning-http2-push-filter</artifactId>
               <version>0.0.1</version>
         </dependency>

If your project has a `web.xml` you can add the following:

        <filter>
                <filter-name>inMemoryLearningPushFilter</filter-name>
                <filter-class>com.helospark.http2.push.filter.InMemoryLearningPushFilter</filter-class>
        </filter>
        <filter-mapping>
                <filter-name>inMemoryLearningPushFilter</filter-name>
                <url-pattern>/*</url-pattern>
        </filter-mapping>

You can configure it's behavious with several servlet init parameter

 - TBD

## Testing

For testing it is best to temporarily disable caching in your browser.

In your browser call a link on your webserver

 - In the first requests you should see that your browser has initiated request for every resource
 - After the second request you should see that you get a lot of the same resources as in the first request, but via push

How push is shown differ by browser. For example in Chrome `Push /` is shown in initiator field in Network tab, like:

![Chrome image](https://raw.githubusercontent.com/helospark/learning-http2-push-filter/master/documentation/chrome_push.png)

When you are testing locally you probably will not see large speed improvements, afterall lo interface has very minimal latency
 you should check with actual network to see improvements.

With the fast lo interface you will also see, that not every resource is loaded via push, that's because sending back resource 1 could
 could cause the browser to ask for a linked resource 2, with so small latency this request may be processed by your webserver before
 the push can occur. This is generally no problem, more resources will be pushed once actual latency is is occurring.

## Troubleshooting

  - Check logs
  - If there are not enough log to debug the problem, enable DEBUG logging for package `com.helospark.http2.push.filter`.


## Required dependencies

  - This filter may be used with any servlet container the provide (at minimum) servlet-api version 4.0
  - Logger uses slf4j, can be configured by any slf4j implementation

## Credits

This project (shamelessly) got the inspiration from Jetty's PushServletFilter implementation.
