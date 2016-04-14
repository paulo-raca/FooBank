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

- It's OK to overdraft an account.



## Running the server

You can use maven to start the server
```bash
sudo mvn -f foobank-api/pom.xml exec:java -Dfoobank.uri=http://0.0.0.0:80/foobank/

```

## Using the API

A few examples of how to use the REST api with `curl`.

```bash
# Creates an account for user #1 (Jane Woods), initial deposit of $565
$ ACCOUNT_1=`curl -XPOST -d "customerId=1&balance=565" http://foobank.paulo.costa.nom.br/foobank/account`
$ echo $ACCOUNT_1
10

# Check the balance
$ curl http://foobank.paulo.costa.nom.br/foobank/account/$ACCOUNT_1/balance
565.0

# Check the history - It only has the initial (Which does not have a sourceAccountId)
$ curl http://foobank.paulo.costa.nom.br/foobank/account/$ACCOUNT_1/history
[{"amount":565.0,"destinationAccountId":10}]

---

# Create another account for user #2 (Michael Li), with no funds
$ ACCOUNT_2=`curl -XPOST -d "customerId=2" http://foobank.paulo.costa.nom.br/foobank/account`
$ echo $ACCOUNT_2
12

# Check the balance -- Should be zero
$ curl http://foobank.paulo.costa.nom.br/foobank/account/$ACCOUNT_2/balance
0.0

# Check the history - It has no events
$ curl http://foobank.paulo.costa.nom.br/foobank/account/$ACCOUNT_2/history
[]

---

# Transfer some money from first to second account
$ curl -XPOST -d "destAccountId=$ACCOUNT_2&amount=10" http://foobank.paulo.costa.nom.br/foobank/account/$ACCOUNT_1/transfer

# Check balances again
$ curl http://foobank.paulo.costa.nom.br/foobank/account/$ACCOUNT_1/balance
555.0
$ curl http://foobank.paulo.costa.nom.br/foobank/account/$ACCOUNT_2/balance
10.0

# Check transfer history again
$ curl http://foobank.paulo.costa.nom.br/foobank/account/$ACCOUNT_1/history
[
  {"amount":565.0,"destinationAccountId":10},
  {"amount":10.0,"destinationAccountId":12,"sourceAccountId":10}
]
$ curl http://foobank.paulo.costa.nom.br/foobank/account/$ACCOUNT_2/history
[{"amount":10.0,"destinationAccountId":12,"sourceAccountId":10}]
```
