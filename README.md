# Rest Resource

Rest Resource is a framework wich whose goal is to provide an easy and simple
way to access RESTful Web Services Apis like Rails. It's intended to work like
ActiveResource.

This framework does not have all the functionalities it's intended to have yet.
However, some of them are (those explained below), and can be already used.

## Mapping RESTful resources as models

To make a class behave like a RESTful resource model, it needs to implement
some methods:

```java
public class Person {
  int id;
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
p = RestResource.save(p); // Triggers PUT http://localhost:4567/people/:PERSON_ID.json
```

## The ID field

In order to update resources, the class must have an 'id' field.

```java
public class Person {
  int id;
}
```

However, you may have another name for the id field. In this case, you need to
explicitly mark the id field annotating it with `restresource.Id`.

```java
import restresource.Id;

public class Person {
  @Id
  int code;
}
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

Please use TDD. In order to test, you will need to install [ruby][1], [bundler][2]
and [sinatra][3]. To install sinatra, with ruby and bundler already installed,
run:

```sh
cd server
bundle install
```

The integrations tests will run the server in the `integration-test` phase
and shut it down when tests are done. Right now, it only works in *nix
systems but pull-requests are accepted.

[1]: https://www.ruby-lang.org
[2]: http://bundler.io/
[3]: http://www.sinatrarb.com/
