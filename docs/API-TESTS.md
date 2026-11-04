# API test examples (success and failure)

Start the app, then run these in order (IDs assume a fresh database). The responses below are what the API is designed to return – replace/add your own screenshots after running them.

## Customers
```bash
curl -X POST localhost:8080/api/customers/register -H "Content-Type: application/json" \
 -d '{"customerName":"John Doe","email":"john@example.com","password":"pass123","mobile":"9876543210","city":"Jaipur"}'

curl -X POST localhost:8080/api/customers/register -H "Content-Type: application/json" \
 -d '{"customerName":"Jane","email":"jane@example.com","mobile":"12345"}'

```

## Products
```bash
curl -X POST localhost:8080/api/products -H "Content-Type: application/json" \
 -d '{"productName":"Wireless Mouse","description":"Ergonomic 2.4G mouse","price":25.5,"quantity":100,"category":"Electronics"}'

curl localhost:8080/api/products/search?name=mouse
curl localhost:8080/api/products/category/Electronics
```

## Cart
```bash
curl -X POST "localhost:8080/api/cart/add/1/1?quantity=2"

curl -X POST "localhost:8080/api/cart/add/1/1?quantity=1"

curl -X POST "localhost:8080/api/cart/add/1/1?quantity=100000"

curl localhost:8080/api/cart/1
curl -X PUT "localhost:8080/api/cart/update/1?quantity=4"
curl -X DELETE localhost:8080/api/cart/remove/1
curl -X DELETE localhost:8080/api/cart/clear/1
```

## Orders
```bash
curl -X POST localhost:8080/api/orders/place/1

curl -X POST localhost:8080/api/orders/place/1

curl localhost:8080/api/orders/customer/1        # history
curl -X PUT localhost:8080/api/orders/cancel/1   # status CANCELLED, stock restored
```
