# parser combinator

A parser combinator is a higher-order function that accepts several parsers as input and returns a new parser as 
its output, it is one of the most beautiful applications of functional programming. They offer an internal DSL to 
use for designing external DSLs so you don’t have to implement your own language infrastructure as you do with other techniques of external DSL design


This is a very simple implementation of parser combinator with Kotlin, the goal is to implement a parser to read
a configuration file with multiple lines of key-value pairs, e.g:

```
height:100
width:50
depth:12
```


References:

- https://en.wikipedia.org/wiki/Parser_combinator
- https://martinfowler.com/dslCatalog/parserCombinator.html
