# Service composition

The first entry is static entry.

Test scenario below shows a creation of a new payment.
```
.\grpcurl.exe -plaintext  localhost:8000 model.PaymentService.getPayments

{                                         
  "payments": [                           
    {                                     
      "orderId": "1234",                  
      "cardNumber": "1234-1234-1234-1234",
      "cardOwner": "TojsemJa"
    }
  ]
}

.\grpcurl.exe -plaintext -d '{\"orderId\": \"1234\", \"cardNumber\": \"1234-1234-1234-1234\", \"cardOwner\": \"Ca
rdOwner\"}'  localhost:8000 model.PaymentService.createPayment  

{
  "code": "0",
  "message": "Payment created!"
}

.\grpcurl.exe -plaintext  localhost:8000 model.PaymentService.getPayments

{                                                               
  "payments": [
    {
      "orderId": "1234",
      "cardNumber": "1234-1234-1234-1234",
      "cardOwner": "TojsemJa"
    },
    {
      "orderId": "1234",
      "cardNumber": "1234-1234-1234-1234",
      "cardOwner": "CardOwner"
    }
  ]
}
```

```
.\grpcurl.exe -plaintext -d '{\"orderId\": \"1234\", \"cardNumber\": \"1234-1234-1234-1234\", \"cardOwner\": \"Ma
rtinOhnút\"}'  localhost:8000 model.PaymentService.createPayment

{
  "code": "1",
  "message": "Wrong card credentials!"
}

```
