# FooBank
A little exercise to implement a (VERY) simplified banking API using Jersey (For exposing REST APIs) and JPA (For persistency)

It is very, _very_ simple, therefore it has a number of limitations:

- APIs are very incomplete. 

  E.g.: You can create a Bank account, but you cannot delete it. And it can only belong to one of the 4 pre-created customers.
  
- I gave no attention to Logging, Exception handling, etc, etc, and are mostly limited to the defaults.

- Database is stored in-memory.

  If you really wanted to, persistence it is only a matter of modifying `persistence.xml`.
  
- It's a single, monolithic project, with a monolithic implementation.

  It's really small and simple. I don't see the need for DAOs, Filters, Factories, etc.
