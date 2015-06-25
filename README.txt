In order to get up and running quickly, I built this sample NotesService Java application by starting with a Maven Archetype. Specifically, I used: jersey-quickstart-grizzly2 (version 2.18).

The REST implementation is provided by Jersey, an open source framework that meets JAX-RS specification. The JSON provider is MOXy (all but provided by Jersey and the Maven Archetype I selected). The archetype also includes Grizzly, a lightweight HTTP container. My sample code would need to be repackaged in order to run in a Servlet container (like Tomcat).

It is very easy to test and run from the project root.
To compile and execute the JUnit tests I have provided, 
     execute: mvn clean test
To start the application and embedded Grizzyly container, 
     execute: mvn exec:java

Grizzly is set to run on port 8080 by default.
You may access the 'notes' API at http://localhost:8080/api/notes.

Example curl commands are as follows: (I ended up having to do this on a windows box.. I hugely prefer unix-style OSes)

curl -i -H "Content-Type: application/json" -H "Accept: application/json" -X POST -d "{\"body\" : \"Pick up milk!\"}" http://localhost:8080/api/notes

curl -i -H "Content-Type: application/json" -H "Accept: application/json" -X POST -d "{\"body\" : \"Don't forget the eggs!\"}" http://localhost:8080/api/notes

curl -i -H "Accept: application/json" -X GET http://localhost:8080/api/notes/1

curl -i -H "Accept: application/json" -X GET http://localhost:8080/api/notes

curl -i -H "Accept: application/json" -X GET http://localhost:8080/api/notes?query=forget

Implementation Notes:

First, the service calls on to a simple NotesDAO meant for demo purposes, only.
It manages the Note POJOs in a ConcurrentHashMap for in-memory access (Data will not persist after stopping the app).

The REST implementation is completely contained in NotesService.java. If needed, it can be split into interface, API, and Impl at a later time.
I included update and delete methods to complete the API functionality.
I made an effort to include some basic error handling. Without any knowledge of local practice, I just tried to match the REST request with an appropriately typed Exception (with a status for the client).
Included JUnit tests only cover the NotesService. I chose not to add coverage for the NotesDAO (the DAO is a throwaway to validate the NotesService implementation).

Additional Questions (and  my answers):

How well does your note-searching-api scale or not scale? How would you make your search more efficient?
My search is either a) fetching from a static HashMap by key (fast), or b) looping through the entire set of values (in the case of the query parameter). Admittedly, it is not deigned to scale. If designed for a distributed platform, I would have made the DAO responsible for scaling appropriate to the specific platform. For a distributed database, the distribution key for the table might be a hash of the object id, in which case the id-based search query would specify the exact partition (or segment) to search, and the query-parameter search would run against all the partitions individually, but in parallel.

How would you add security to your API?
Hopefully, the security needs of the API would be based on an reputable standard. Jersey is a JAX-RS implementation that includes support for accessing a SecurityContext from the container. OAuth is also supported. If a custom security API is available, special code would have to be added to access and evaluate special header information included in the request. After the Authentication and Authorization is established, the notes themselves could be encrypted to increase privacy and security.

What features should we add to this API next?
I added update and delete functionality.
I would add JavaDoc, next. I don't usually comment code unless I feel the need to explain somehting. But, I really
prefer to have something like an API have documentation regarding purpose, usage, etc.

How would you test the API?
I included JUnit tests to cover many cases, including error cases. These test will actually stand up the Grizzly container and execute against the service directly. I originally wrote the tests to call directly against the Java, but after learning more about the maven archetype, I was happy to see that the service was actually being called as a web target. In this regard, it's really more of an integration test than a simple unit test.

Comments:

I had fun doing this! The first hurdle I faced was actually setting up a modern/current development environment.
My hardware is really out of date. I ended up working on a Windows Vista machine.. far different from the Linux or Yosemite Mac environments I am used to!
I have known about Maven archetypes for a while, but this is the first time I really tried to use them.
Massive confusion set in when I tried to execute the first 'curl' command in the assignment document.
I finally figured out that I needed to add a header for Content-Type.
It was all test-driven development after that!

Thanks for reading,
  -Karlan
