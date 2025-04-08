<p align="center">
 <img src="https://github.com/AndrewVolostnykh/viewton/blob/main/.github/logo/viewton_sp.png" align="center" alt="Viewton Library" />
</p>

<p align="center">
  <h3 align="center">Dynamic SQL queries for REST and IPC</h3>
</p>

---

<p align="center">
  <img alt="Tests Status" src="https://github.com/AndrewVolostnykh/viewton/actions/workflows/maven.yml/badge.svg" />
  <img alt="Made with spring" src="https://img.shields.io/badge/Made with-Spring-mediumseagreen.svg" />
  <img alt="Made with Hibernate" src="https://img.shields.io/badge/Made with-Hibernate-darkgoldenrod.svg" />
  <img alt="MIT Lincense" src="https://badgen.net/pypi/license/pip" />
</p>

## What is Viewton?

Viewton is a library designed for extracting data from databases using REST API (HTTP). It significantly
simplifies data retrieval operations, freeing the code from the need to manually construct complex queries involving
multiple filtering fields, sorting, pagination, and more. It is designed for use with Hibernate and SQL databases.

> See [guide](docs/GUIDE.md), [examples](docs/REQUEST_EXAMPLES.md) and [coming features](docs/COMING_SOON.md)

## When to Use Viewton?

When you need to request data from the back end using different filters. Library aggregate all
common cases of querying data: filtering, count, sum, aggregation, etc.

## Core Features

- **Field Filtering**: Easily filter results based on field values.
- **Pagination**: Implement pagination to control the amount of data retrieved.
- **Field Selection**: Specify exactly which fields should be returned in the query results.
- **Sorting**: Sort results by specific fields, either ascending or descending.
- **Count**: Retrieve the count of entities that match the query criteria (`count(*)`).
- **Distinct**: Get distinct values for specific fields.
- **Summation**: Calculate the sum of numeric field values using `sum(...)`.
- **Avg**: Calculate average value of the specified field.
- **Ignore case**: Ignores case of string entries.
- **Equals by pattern**: search for entities by not full string value entry.

## Simple Usage Examples

### Example 1: Using URL Parameters

Consider an API endpoint for retrieving payment data:

```
domain.com/payments?
page_size=50&page=1
&count=true&distinct=true
&attributes=currencyCode,paymentSum,rate,status
&sum=paymentSum
&sorting=-conclusionDate,id
&userId=111 & userEmail=someEmail@mail.com & paid=true & paymentSum=>=1000 & userName=Some% & authorEmail=^ignoreCaseEmail@email.com
&conclusionDate=2025-01-01..2025-01-26
```

In this example, the URL parameters demonstrate the following functionalities:

- **Pagination**: `&pageSize=50&page=1` - first page, 50 records
- **Counting**: `&count=true` - count a number of queried entities
- **Distinct**: `&distinct=true` - query only distinct entities
- **Field Selection**: `&attributes=currencyCode,paymentSum,rate,status` - select only `currencyCode`, `paymentSum`, `rate` and `status` fields
- **Summing**: `&sum=paymentSum,rate` - get a sum of the `paymentSum` and `rate` fields
- **Sorting**: `&sorting=-conclusionDate,id` - sort result by DESC `conlusionDate` and ASC `id`
- **Filtering**: `&userId=111&userEmail=someEmail@gmail.com&paymentSum=>=1000` - find only entities where `userId` equals 111, `userEmail` equals `someEmail@gmail.com` and `paymentSum` greater or equals to 1000
- **Equals with pattern**: `&userName=Some%` - analog to SQL-like pattern, select entities where `userName` starts with `Some`
- **Ignore case**: `authorEmail=^ignoreCaseEmail@email.com` - ignores case of your value and DB's value, so select entities where `authorEmail` equals to `ignoreCaseEmail@email.com` but ignoring case

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


## How to use it in an application?

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

