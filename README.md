# Viewton Library [![Java CI with Maven](https://github.com/AndrewVolostnykh/viewton/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/AndrewVolostnykh/viewton/actions/workflows/maven.yml)

Viewton is a library designed for extracting data from databases by dynamically generating queries. It significantly
simplifies data retrieval operations, freeing the code from the need to manually construct complex queries involving
multiple filtering fields, sorting, pagination, and more. This is particularly useful when certain parameters should be
ignored if not specified.

> See [guide](docs/GUIDE.md) and [coming features](docs/COMING_SOON.md)

## How to use it in application?

Add the source code or dependency to your project 
and annotate the root application class or an appropriate configuration class with `@EnableViewton`.

Example:
```java
import config.com.viewton.EnableViewton;
import org.springframework.context.annotation.Configuration;

@EnableViewton
@Configuration
public class SomeConfiguration {
    ...
}
```

## How Does Viewton Work?

Viewton simplifies the process of constructing queries for interacting with databases by dynamically creating the necessary components like filters, pagination, sorting, and field selection. It is designed primarily for use with Hibernate and SQL databases, and it is most effective when used with database views, given their optimization potential. However, the library is also flexible enough to work with regular entities.

## When to Use Viewton?

When you need to request data from back-end using different filters. Library aggregate all
common cases of querying data: filtering, count, sum, aggregation

- **Primary Usage**: Viewton is designed for databases using Hibernate and SQL.
- **Recommended Setup**: Using database views for optimized performance, though it works just as well with regular entities.

## Core Features

- **Field Filtering**: Easily filter results based on field values.
- **Pagination**: Implement pagination for controlling the amount of data retrieved.
- **Field Selection**: Specify exactly which fields should be returned in the query results.
- **Sorting**: Sort results by specific fields, either ascending or descending.
- **Count**: Retrieve the count of entities that match the query criteria (`count(*)`).
- **Distinct**: Get distinct values for specific fields.
- **Summation**: Calculate the sum of numeric field values using `sum(...)`.
- **Ignore case**: Ignores case of string entries
- **Equals by pattern**: search for entities by not full string value entry

## Simple Usage Examples

### Example 1: Using URL Parameters

Consider an API endpoint for retrieving payment data:

`{{api-url}}/payments?page_size=50&page=1&count=true&distinct=true&attributes=currencyCode,paymentSum,rate,status&totalAttributes=paymentSum&conclusionDate=2025-01-01..2025-01-26&sorting=-conclusionDate,-id&userId=111&userEmail=someEmail@mail.com&paid=true&paymentSum=>=1000&userName=Some%&authorEmail=^ignoreCaseEmail@email.com`

In this example, the URL parameters demonstrate the following functionalities:

- **Filtering**: `&userId=111&userEmail=someEmail@gmail.com&paymentSum>=1000`
- **Sorting**: `&sorting=-conclusionDate,-id`
- **Summing**: `&totalAttributes=paymentSum`
- **Counting**: `&count=true`
- **Distinct**: `&distinct=true`
- **Field Selection**: `&attributes=currencyCode,paymentSum,rate,status`
- **Pagination**: `&pageSize=50&page=1` (first page, 50 records)
- **Equals with pattern**: `&userName=Some%` analog to SQL like patterns
- **Ignore case**: `authorEmail=^ignoreCaseEmail@email.com` ignores case of your value and DB's value

### Example 2: Using ViewtonParamsBuilder for IPC

In the case of an IPC (Inter-process Communication) query, the same URL query can be constructed using the
`ViewtonParamsBuilder`:

```java
Payment.ParamsBuilder()
  .userId().equalsTo(111L)
  .userEmail().equalsTo("someEmail@gmail.com")
  .paymentSum().greaterThanOrEquals(1000)
  .userName().equalsTo('Some%')
  .antoherEmail().ignoreCase().equalsTo('ignoreCaseEmail@email.com')
  
  .conclusionDate().descSorting()
  .id().ascSorting()
  
  .count().distinct()
  .attributes((ParamsBuilder builder) -> List.of(builder.currencyCode(), builder.paymentSum(), builder.rate(), builder.status()))
  .totalAttributes((ParamsBuilder builder) -> List.of(builder.paymentSum))
  
  .page(1).pageSize(50)
  .build()
```

This example demonstrates how the same query logic can be implemented using Viewton’s API, utilizing `ViewtonQueryBuilder` to
build the query components in a programmatic way.
