# SWDesign2021-Refactoring

**What was done:**

- An HTTP Client was added to interact with the application (since server was a 'black-box' at the first steps).
- A `Product` record was proposed as a future domain-level object (it was first introduced in tests and then, during
  refactoring, moved into the server itself).
- One unit test checks overall system response (I like property tests more in this case).
- Five property tests were added, each of them compares result of Application's response with the trivial algorithm
  results. It covers both get/add methods and all query methods.
- To start server (as it was a blackbox) a special `SingletoneServerStarter` was added, static initialization of which
  during test phase enforces Server to start in a different thread. A 'barrier' synchronization primitive was used to
  synchronize main test thread and server thread. Some delay (using `Thread.sleep()`) was added to ensure server thread
  has started normally (after invocation of `Main.main()` method).
- Application responsibilities were split into multiple categories, according to the MVC paradigm:
    - A Model level contains domain objects (here `Product` was moved during refactoring).
    - A View level contains classes that are responsible for the HTTP response rendering.
    - A Controller level contains all the technical items that power drive this application. It has:
        - A database interaction layer: `DBConnectionProvider` is responsible for the JDBC connection establishment
          and `ProductRepository` manages products that are stored in database (translates business-level interaction
          into DB-level).
        - An exception layer that describes different kinds of exceptional situations that application might have faced.
        - A servlet level that controls state of Model during the interaction with the users through View level.
- Code duplication was shrunk to the most possible extent, except for max-min queries, generalization of which at some
  points might have produced a lot more code.
- Fragments with different responsibilities were moved into different layers.
- JavaDoc was added.

**Launch commands:**

- `mvn build` will compile project.
- `mvn test` will launch Unit and Property tests.
- `mvn clean` will remove all built files.
- `mvn javadoc:javadoc` will build API Documentation for all public entities in source. The resulting site is expected
  to be located in `./target/site/apidocs/index.html`.

**Notes:**

- `SingletoneServerStarter` was used instead of `@BeforeAll` because JUnit executes tests in parallel there is no
  ordering between `@Before`-like and `@After`-like calls in different tests. Here this is an issue, because only one
  server could be running at the moment: two servers cannot share one HTTP port `8081`.
- Junit 4 was used instead of Junit 5, because Property tests framework Quickcheck is dependent on Junit 4 and there is
  no point to mix JUnit 5 in Unit tests and JUnit 4 in Property tests. 