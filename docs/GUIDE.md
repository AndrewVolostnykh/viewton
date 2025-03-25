# Guide
This guide on Viewton will reveal all the functions and capabilities of the library.

## Selecting

Viewton allows you to automatically select all fields from an entity without
specifying them or describing special functionality for this operation.

When we need to select all fields form entity it can be performed using next code:

```java
viewtonRepository.list(Map.of(),SomeEntity.class);
```

Alternatively, let's imagine there is an endpoint `{basic-url}/some-entity` that accepts 
a `Map<String, String>` as a `RequestParam` and calls the `ViewtonRepository`.
From the client side, this request would look like a simple call to this endpoint.
As a result, a `ViewtonResponse` will be returned with all the data from the database 
for the specified entity and its fields.

### Exact fields

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
To enable this mode symbol `^` have to be used, it applies to every. 
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

## Sorting