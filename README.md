# Short UUID generator

## How it works

It uses the current date and time and a round-robin counter to generate an id.
You can choose between Base 62 alphanumeric or Base 36 alphanumeric

```
    ShortUUIDGenerator shortUUIDGenerator = new ShortUUIDGenerator();
    shortUUIDGenerator.generate(LocalDateTime.now());
```

Example Ids:

```
    Base 62: 3RYsrI001
    Base 36: 1G6T1C0001
```

The id is unique as long it is created in the same instance and you don't exceed 
the maximum number of ids per second. 

For Base 62 with 3 digits it is more than 200k numbers per second, so this should be fine for must use cases.

## Unique across machines

This id is of course not unique across machines. You can use it for unit or integration tests.
For a more sophisticated solution you might want to add additional random or environment elements as prefix to it. 