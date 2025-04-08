# Guide

This guide on Viewton will reveal common cases of usage, all the functions and capabilities of the library.

### Table of Contents

1. [Implementing into an application](#implementing-into-an-application)
2. [Usage](#usage)
3. [Selecting fields](#selecting-fields)
4. [Count](#count)
5. [Distinct](#distinct)
6. [Filtering](#filtering)
   1. [Equals](#equals)
      1. [Like](#like)
      2. [Ignore case](#ignore-case)
   2. [Not equals](#not-equals)
   3. [Greater than](#greater-than)
   4. [Less than](#less-than)
   5. [Greater than or equals](#greater-or-equals-to)
   6. [Less than or equals](#less-or-equals-to)
   7.

> Review the [examples](REQUEST_EXAMPLES.md) for a clearer understanding of the queries.

## Implementing into an application

Implementing Viewton to project is pretty simple. Special annotation `EnableViewton` based on `ComponentScan` so
adding it to base class or configuration tells Spring where to look Viewton's dependencies.

It should look like this:

```java
import com.viewton.config.EnableViewton;
import org.springframework.context.annotation.Configuration;

@EnableViewton
@Configuration
public class SomeConfiguration {
   // configuration
}
```

or for base class:

```java
import com.viewton.config.EnableViewton;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableViewton
@SpringBootApplication
public class Application {
   // application startup details
}
```

## Usage

`ViewtonRepository` is a class is an API for building queries. Basically it requires just `Map<String, String>` with
needed modificators, filters, etc. This class can be autowired to needed component.

Example:

```java
import com.viewton.ViewtonRepository;
import com.viewton.dto.ViewtonResponseDto;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/some-entities")
public class SomeEntityController {
   private final ViewtonRepository viewtonRepository;

   @Autowired
   public SomeEntityController(ViewtonRepository viewtonRepository) {
      this.viewtonRepository = viewtonRepository;
   }

   @GetMapping
   public ViewtonResponseDto<SomeEntity> list(@RequestParams Map<String, String> params) {
      return viewtonRepository.list(params, SomeEntity.class);
   }
}
```

So in this example controller has a list method, which requires parameters and will return ViewtonResponse<SomeEntity>.

On the client side need to just implement request with needed modificators and filters to query needed data.

Example:

```http request
https://sometestdomain.com/some-entities?deleted=false&number=>1000
```

This URL would be used to select all SomeEntities where field `deleted` is false and `number` greater than 1000.

# Features

### Selecting Fields

Viewton returns all entities fields on request.
To select only specific fields for an entity, you need to use the `attributes` parameter.
On the client side endpoint call will be next:
```
{basic-url}/some-entity?attributes=someAttribute,someAnotherAttribute
```
In this case we have specified that only `someAttribute` and `someAnotherAttribute` 
should be mapped and all other should be ignored.

### Count
Viewton allows to count all the entities selected by filters (see how filters work below).
To perform `count` query just specify param `count=true`
So endpoint call should be like this:
```
{basic-url}/some-entity?count=true
```
The backend response will be:

```json
{
  "list": [
    ...entities...
  ],
  "sum": null,
  "count": 10
}
```
So here we can see that request performed and returned number of all table entries
(10 in this case)

### Distinct
Viewton allows to select only distinct rows. To enable this mode just specify `distinct=true`,
so the result entities will be distinct.

## Filtering
Viewton has a functionality to filter DB rows.
As an example let's take next entity:
```java
public class SomeEntity {
    private Long id;
    private String someAttribute;
    private String someAnotherAttribute;
    private LocalDateTime someDate;
    private Long someNumber;
}
```

### Equals

To select entities with specified field's value we have to use next syntax:
```
{basic-url}/some-entity?someAttribute=value
```
So there we can see that entities which `someAttribute` 
field will be equals to `value` will be selected only.

##### Like
Also, Viewton has an opportunity to select data by pattern (`like` operation in SQL).
Symbol `%` (the same as SQL syntax) specifies needed pattern.
```
{basic-url}/some-entity?someAttribute=%value
```
The result will be the same if we perform `select * from some_entite where someAttribute like %value`.
`%` can be used as in the SQL, so correct patterns will be like next: `%value`, `%value%`, `value%value`, etc

##### Ignore case

When selecting fields viewton allows to search entities ignoring case of selecting and stored values.
To enable this mode symbol `^` have to be used.
The mode is applied to each attribute individually.
For instance:
```
{basic-url}/some-entity?someAttribute=^value&someAnotherAttribute=^anotherValue
```
In this case all entities will be selected filtering by `someAttribute` value and `someAnotherAttribute` value
ignoring its case
Or:
```
{basic-url}/some-entity?someAttribute=^value&someAnotherAttribute=anotherValue
```
For `someAttribute` ignore case is applied, for `someAnotherAttribute` doesn't.

### Not equals

Not equals functionality works the same as equals, but just specifying `<>` before value.
```
{basic-url}/some-entity?someAttribute=<>value
```
So response will contain entities that `someAttribute` value will not be `value`

> [!NOTE]\
> Modificators `like` and `ignore case` also applicable to 'Not equals'

### Greater than
### Less than
### Greater or Equals to
### Less or Equals to
### Or
### Range

## Aggregate functions
### Sum
### Min
### Max
### Avg
### Group by

## Pagination

The Viewton library allows for pagination of queries,
meaning it splits them into pages to reduce the load on the system and/or the database.

Pagination is controlled using two parameters: page_size and page.

- `page_size` specifies the number of entities to be returned in the query.

- `page` represents the page number.

For example, the query would look like this: page_size=50&page=2, which means the second page with 50 entities.

By default, Viewton sets the page size to 50.
This means that if page_size is not provided,
only 50 entities will be returned from the query.

The application attribute viewton.request.default-page-size allows you to specify a default page size.

Pagination is ignored, and all entities will be returned if page_size is set to -1, either in the request or as the
default value.

## Sorting

In the query, you can specify fields and the order in which they should be sorted. This is done using the sorting
parameter, where the values are attributes separated by commas. If a `-` sign precedes an attribute, it means the
sorting will be in DESC (descending) order. If there is no `-` sign, the sorting will be in ASC (ascending) order.

Example: `sorting=-id,date`

In this case, the results will be sorted by two parameters: id and date. The id will be sorted in DESC order, while date
will be sorted in ASC order.