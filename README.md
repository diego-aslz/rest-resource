
# Rest Resource

Rest Resource is a framework wich whose goal is to provide an easy and simple
way to access RESTful Web Services Apis like Rails. It's intended to work like
ActiveResource.

This framework does not have all the functionalities it's intended to have yet.
However, some of them are (those explained below), and can be already used.

## Mapping RESTful resources as models

To make a class behave like an RESTful resource model, it needs to implement
some methods:

```java
public class Person {
  @Expose
  String name;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
```

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

Please use TDD. In order to test, you will need to run the the fake web service
within the `server` directory like this:

```
ruby server/fake_server.rb
```

This fake server is built on [Sinatra](http://www.sinatrarb.com/).
