**Basic entity**

Java class:

```java
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime birthdate;
    @AvgAlias(mapTo = "randomNumberAvg")
    private Long randomNumber;
    private Double randomNumberAvg;
}
```

JSON:

```
{
  "id": <long>,
  "firstName": <string>,
  "lastName": <string>,
  "email": <string>,
  "birthdate": <date>,
  "randomNumber": <number>,
  "randomNumberAvg": <number>
}
```

### Equals, Like, Or, Count, Exact attributes

- equals: `firstName=Stew`
- or: `firstName=Stew|John`
- like: `email=%@testmail.com`
- exact attributes: `attributes=firstName,lastName,email,id`

```
/some-entity?firstName=Stew|John&email=%@testmail.com&attributes=firstName,lastName,email,id&count=true
```

Response (ignoring nulls):

```json
{
  "list": [
    {
      "id": 1,
      "firstName": "Stew",
      "lastName": "Smith",
      "email": "stew.smith@testmail.com"
    },
    {
      "id": 2,
      "firstName": "John",
      "lastName": "Doan",
      "email": "john.doan@testmail.com"
    }
  ],
  "count": 2
}
```

### Less than, Range, Sum, Sorting, Pagination

- less than: `randomNumber=>20`
- range: `birthdate=1992-01-20T20:29:20.536783..2000-01-20T20:29:20.536784`
- sum: `sum=randomNumber`
- sorting: `sorting=-birtdate`
- pagination: `page=2&page_size=2`

```
/some-entity?randomNumber=>20&birthdate=1992-01-20T20:29:20.536783..2000-01-20T20:29:20.536784&sum=randomNumber&sorting=-birthdate&page=2&page_size=30
```

Response (ignoring nulls):

```json
{
  "list": [
    {
      "id": 15,
      "firstName": "Stew",
      "lastName": "Smith",
      "email": "stew.smith@testmail.com",
      "birthdate": "1995-05-10T00:00:00.000000",
      "randomNumber": 250
    },
    {
      "id": 2,
      "firstName": "John",
      "lastName": "Doan",
      "email": "john.doan@testmail.com",
      "birthdate": "1999-10-09T00:00:00.000000",
      "randomNumber": 750
    }
  ],
  "sum": [
    {
      "randomNumber": 1000
    }
  ],
  "count": 2
}
```