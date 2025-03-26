# Small table
### Conditions
- **DB contains** 284 entities,
- **tested** 10 000 times
- **query**: 
```
count=true,
distinct=true,
sum=randomNumber,
email=%@gmail.com,
birthdate=1992-01-20T20:29:20.536783..2000-01-20T20:29:20.536784
randomNumber > 9
```

### Result:

- Spring Data Named Query: 165.3214 ms
- Viewton: 165.8923
- SQL: 114.2617