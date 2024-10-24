# Coding assignment

- Design a REST API which takes JSON as input
- JSON should be able to accept a list of transactions
- Each transaction includes
  - Amount
  - Account Number
  - Debit / Credit
  - Date of transaction
  - Currency
- Input can have multiple transactions per account number.
- Input should always generate one or more XML files, or return an error.
- Output should be a valid XML file according to provided XML Schema.
- Output is one XML file per transaction date.
- Output is saved in a directory on the local file system.
- Header contains the transaction summary. Other details given in schema.
- Output contains one `transactionDetail` block per account number.
- Unit and integration tests

## Example request

[http://localhost:8080/transactions/import](http://localhost:8080/transactions/import)

```json
[
  {
    "transactionAmount" : {
      "amount": 100.0,
      "currency": "SEK"
    },
    "accountNumber" : "1234567890",
    "debitCredit" : "DEBIT",
    "transactionDate" : "2024-10-23T12:51:50"
  }
]
```

