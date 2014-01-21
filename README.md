# Rest Resource

Rest Resource is a framework wich whose goal is to provide an easy and simple
way to access RESTful Web Services Apis like Rails. It's intended to work like
ActiveResource.

This framework does not have all the functionalities it's intended to have yet.
However, some of them are (those explained below), and can be already used.

## Mapping RESTful resources as models

To make a class behave like a RESTful resource model, it needs to implement
`restresource.Element`. Also, it needs to implement some static methods:

```java
public class Person implements restresource.Element {
  int id;
  @Expose
  String name;

  // Constructors, getters and setters (not really needed)

  /**
   * Determines the address where the RESTful Web Service is.
   */
  public static String getSite() {
    return "http://localhost:4567/";
  }

  /**
   * Determines the name of the collection to be used in the URL for the
   * calls. This method is optional. If it's not present, the name of class,
   * lower cased, plus "s" will be used.
   */
  public static String collectionName() {
    return "people";
  }
}
```

With that in place, it's possible to call:

```java
Person p = RestResource.find(1, Person.class); // Triggers GET http://localhost:4567/people/1.json

List<Person> l = RestResource.all(Person.class); // Triggers GET http://localhost:4567/people.json

p = Person.new();
p.setName("John");
p = RestResource.save(p); // Triggers POST http://localhost:4567/people.json
p = RestResource.save(p); // Triggers PUT http://localhost:4567/people/:ID.json
RestResource.destroy(p);  // Triggers DELETE http://localhost:4567/people/:ID.json
```

## The ID field

In order to access single resources, the class must have an 'id' field.

```java
public class Person implements Element {
  int id;
}
```

However, you may have another name for the id field. In this case, you need to
explicitly mark the id field annotating it with `restresource.Id`.

```java
import restresource.Id;

public class Person implements Element {
  @Id
  int code;
}
```

## Custom methods

It's possible to invoke custom URLs. In these cases, the response will
be returned as String and you will have to handle it by yourself. Don't worry,
there is some helper methods to do it for you:

```java
String response = RestResource.get(Person.class, "hired");
// Triggers GET http://localhost:4567/people/hired.json
List<Person> people = loadCollection(Person.class, response);

// With parameters
String response = RestResource.get(Person.class, new ParamGenerator("from",
    "2014-01-01").append("to", "2014-01-08"), "hired")
// Triggers GET http://localhost:4567/people/hired.json?to=2014-01-08&from=2014-01-01
List<Person> people = loadCollection(Person.class, response);

RestResource.post(Person.class, "hire", new Person(1, "John"));
// Triggers POST http://localhost:4567/people/hire.json with the object in the body as JSON

RestResource.put(new Person(1), "promote");
// Triggers PUT http://localhost:4567/people/1/promote.json

RestResource.delete(new Person(1), "fire");
// Triggers DELETE http://localhost:4567/people/1/fire.json
```

If you want to see more examples, take a look at the test classes.

## Status Code Exceptions

The methods that communicates with the server throw StatusException when certain
HTTP status codes are received, so as:

* 401: UnauthorizedAccessException;
* 403: ForbiddenAccessException;
* 404: ResourceNotFoundException;
* 422: ResourceInvalidException;
* 400..499: ClientException;
* 500..599: ServerException.

## Dependencies

In order to work, this project depends on:

* Gson from Google to parse JSON;
* JUnit to test.

The dependencies are managed via Maven.

## Contributing

* Fork this repository;
* Clone it to your machine;
* Write your code and **test it**;
* Make a pull request.

Please use TDD. In order to test, you will need to install [Ruby][1], [Bundler][2]
and [Sinatra][3] . To install Sinatra (and any other needed rubygems),
with Ruby and Bundler already installed, run:

```sh
cd server
bundle install
```

The integrations tests will run the server in the `pre-integration-test` phase
and shut it down in `post-integration-test`. Right now, it only works in *nix
systems but pull-requests are accepted.

[1]: https://www.ruby-lang.org
[2]: http://bundler.io/
[3]: http://www.sinatrarb.com/
